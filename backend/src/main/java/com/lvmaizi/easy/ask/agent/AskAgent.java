package com.lvmaizi.easy.ask.agent;

import com.lvmaizi.easy.ask.agent.tools.AskTools;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AskAgent {

    private static final Logger log = LoggerFactory.getLogger(AskAgent.class);

    // 最大迭代次数
    private static final int MAX_ITERATIONS = 10;

    private final ChatClient chatClient;
    private final List<ToolCallback> tools;
    @Getter
    private final List<Message> messages = new ArrayList<>();

    public AskAgent(ChatClient chatClient, List<ToolCallback> tools) {
        this.chatClient = chatClient;
        this.tools = tools != null ? tools : new ArrayList<>();
    }

    @Setter
    private SseEmitter sseEmitter;

    public String run(String question) {
        // 1. 初始化对话历史
        messages.add(new UserMessage(question));
        List<String> toolHistory = new ArrayList<>();

        // 2. ReAct 循环
        for (int iteration = 1; iteration <= MAX_ITERATIONS; iteration++) {

            try {
                AssistantMessage assistantMessage = thinkStreaming();

                // Act & Observe: 检查是否需要调用工具
                if (hasToolCalls(assistantMessage)) {
                    messages.add(assistantMessage);
                    // 执行工具调用
                    String toolResult = executeTools(assistantMessage, toolHistory);

                    // 将工具结果添加到对话历史
                    var toolCall = getToolCall(assistantMessage);
                    messages.add(ToolResponseMessage.builder().responses(
                            List.of(new ToolResponseMessage.ToolResponse(toolCall.id(), toolCall.name(), toolResult)
                            )
                    ).build());

                } else {
                    // 没有工具调用，说明大模型已给出最终答案
                    messages.add(assistantMessage);
                    log.info("\n\nfinal answer：{}", assistantMessage.getText());
                    break;
                }

            } catch (Exception e) {
                log.warn("error: {}", e.getMessage());
                break;
            }
        }
        // 关闭连接
        sseEmitter.complete();
        return messages.getLast().getText();
    }

    private AssistantMessage thinkStreaming() {

        // 设置工具调用选项
        ToolCallingChatOptions options = ToolCallingChatOptions.builder()
                .internalToolExecutionEnabled(false)  // 禁用自动执行，由我们手动控制
                .build();

        Prompt prompt = new Prompt(messages, options);

        // 使用流式响应
        AtomicReference<AssistantMessage> lastMessage = new AtomicReference<>();
        StringBuilder fullText = new StringBuilder();

        Flux<ChatResponse> responseFlux = chatClient.prompt(prompt)
                .toolCallbacks(tools)
                .stream()
                .chatResponse();

        // 处理流式响应
        responseFlux.doOnNext(response -> {
            if (response.getResult() != null) {
                AssistantMessage output = response.getResult().getOutput();

                // 累积文本内容
                String text = output.getText();
                if (text != null && !text.isEmpty()) {
                    fullText.append(text);
                    String sanitizedResponse = text.replace("\n", "[@]").replace("\r", "@|@");
                    try {
                        sseEmitter.send(SseEmitter.event().data(sanitizedResponse));
                    } catch (IOException e) {
                        log.warn("Error sending message: " + e.getMessage());
                    }
                }

                // 保存最后一个消息
                lastMessage.set(output);
            } else {
                  log.warn("No response from OpenAI");
            }
        }).blockLast();

        // 如果有累积的文本且没有工具调用，构建完整的消息
        if (lastMessage.get() != null) {
            AssistantMessage currentMessage = lastMessage.get();
            if (currentMessage.getToolCalls().isEmpty() && !fullText.isEmpty()) {
                // 重新构建包含完整文本的消息
                return AssistantMessage.builder()
                        .content(fullText.toString())
                        .build();
            }
        }

        return lastMessage.get();
    }

    /**
     * 检查是否有工具调用
     */
    private boolean hasToolCalls(AssistantMessage message) {
        return message.getToolCalls() != null && !message.getToolCalls().isEmpty();
    }

    /**
     * 获取第一个工具调用
     */
    private AssistantMessage.ToolCall getToolCall(AssistantMessage message) {
        if (hasToolCalls(message)) {
            return message.getToolCalls().getFirst();
        }
        return null;
    }

    /**
     * Act & Observe 阶段 - 执行工具并观察结果
     */
    private String executeTools(AssistantMessage message, List<String> toolHistory) throws Exception {
        var toolCall = getToolCall(message);

        // 查找并执行匹配的工具
        for (ToolCallback tool : tools) {
            assert toolCall != null;
            if (tool.getToolDefinition().name().equals(toolCall.name())) {
                Object result = tool.call(toolCall.arguments());
                if (toolHistory.isEmpty() || !toolHistory.getLast().equals(toolCall.name())) {
                    sseEmitter.send("> 正在【%s】".formatted(AskTools.getName(toolCall.name())) + "...[@][@]");
                }
                toolHistory.add(toolCall.name());
                return result.toString();
            }
        }

        throw new RuntimeException("未找到工具：" + toolCall.name());
    }
}
