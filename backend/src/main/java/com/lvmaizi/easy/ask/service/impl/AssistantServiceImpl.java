package com.lvmaizi.easy.ask.service.impl;

import com.lvmaizi.easy.ask.agent.AgentFactory;
import com.lvmaizi.easy.ask.agent.AskAgent;
import com.lvmaizi.easy.ask.agent.ExtractionAgent;
import com.lvmaizi.easy.ask.agent.tools.AskTools;
import com.lvmaizi.easy.ask.service.request.ChatCompletionRequest;
import com.lvmaizi.easy.ask.service.request.ChatCreateRequest;
import com.lvmaizi.easy.ask.service.response.BaseResponse;
import com.lvmaizi.easy.ask.service.response.ChatCreateResponse;
import com.lvmaizi.easy.ask.service.response.ResponseBuilder;
import com.lvmaizi.easy.ask.service.AssistantService;
import com.lvmaizi.easy.ask.utils.IdGenerator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AssistantServiceImpl implements AssistantService {

    @Resource
    private AgentFactory agentFactory;

    @Resource
    private AskTools askTools;

    @Override
    public BaseResponse<ChatCreateResponse> createChat(ChatCreateRequest request) {
        ChatCreateResponse responses = new ChatCreateResponse();
        responses.setSessionId(IdGenerator.id());
        return ResponseBuilder.ok(responses);
    }

    @Override
    public SseEmitter chatCompletion(ChatCompletionRequest completionRequest) {
        SseEmitter emitter = new SseEmitter(360000L);
        AskAgent askAgent = agentFactory.getAskAgent(completionRequest.getSessionId());
        askAgent.setSseEmitter(emitter);

        new Thread(() -> {
            askAgent.run(completionRequest.getPrompt());

            ExtractionAgent extractionAgent = agentFactory.getExtractionAgent(completionRequest.getSessionId());
            extractionAgent.run(askAgent.getMessages());
        }).start();
        return emitter;
    }

    @Override
    public String getChatHistory(String sessionId) {
        List<Message> messages = agentFactory.getAskAgent(sessionId).getMessages();

        String result = """
                <html>
                <body>
                <pre>
                
                %s
                
                </pre>
                </body>
                </html>
                """;

        return result.formatted(messages.stream().map(e -> e.getMessageType().name() + ": " + e.getText()).collect(Collectors.joining("\n")));
    }
}
