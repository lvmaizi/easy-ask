package com.lvmaizi.easy.ask.agent;

import com.lvmaizi.easy.ask.agent.tools.AskTools;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Component
public class AgentFactory {

    public final static String ASK_SYSTEM_PROMPT =
            """
            
            # 你是一个客服助手，你有一个资料库，该资料库是一个目录，你应当基于该资料库去回答问题
            
            # 你的核心职责
            1. 根据用户的问题及可使用的工具，收集资料数据
            2. 所有问题都要尽可能的从你有的资料库中去收集
            3. 你可以根据工具检索到的情况多次调用相关工具
            
            
            # 返回约束
            尽可能的根据相关工具检索到正确的资料，若检索到的资料已足够回答用户问题则进行回答
            
            """;

    private static final Map<String, AskAgent> askAgentMap = new LinkedHashMap<>();

    @Resource
    private ChatModel chatModel;

    @Resource
    private EmbeddingModel embeddingModel;

    @Resource
    private AskTools askTools;

    public AskAgent getAskAgent(String sessionId) {
        AskAgent askAgent = askAgentMap.get(sessionId);
        if (askAgent != null) {
            return askAgent;
        }
        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultSystem(p -> p.text(ASK_SYSTEM_PROMPT))
                .build();

        askAgent = new AskAgent(chatClient, askTools.getTools());
        askAgentMap.put(sessionId, askAgent);
        return askAgent;
    }

    public final static String EXTRACTION_SYSTEM_PROMPT =
            """
            
            # 你是一个知识萃取助理，你的职责是根据用户与AI的聊天记录萃取对应的问答对
            
            # 注意事项
            1. 严格按照示例格式输出（json）
            2. 若内容不能明确回答用户诉求，则不进行萃取，返回空数组
            3. query字段可以适当润色重写
            
            # 返回格式（可返回0个或多个问答对）
            [
                {
                    "query":"问题",
                    "answer":"答案（尽可能详细）"
                }
            ]
            
            """;

    private static final Map<String, ExtractionAgent> extractionAgentMap = new LinkedHashMap<>();

    public ExtractionAgent getExtractionAgent(String sessionId) {
        ExtractionAgent extractionAgent = extractionAgentMap.get(sessionId);
        if (extractionAgent != null) {
            return extractionAgent;
        }
        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultSystem(p -> p.text(EXTRACTION_SYSTEM_PROMPT))
                .build();

        extractionAgent = new ExtractionAgent(chatClient, embeddingModel);
        extractionAgentMap.put(sessionId, extractionAgent);
        return extractionAgent;
    }

    public static void clear() {
        Set<String> asks = askAgentMap.keySet();
        for (int i = 0; i < asks.size() - 100; i++) {
            askAgentMap.remove(askAgentMap.keySet().iterator().next());
        }

        Set<String> extractions = extractionAgentMap.keySet();
        for (int i = 0; i < extractions.size() - 100; i++) {
            extractionAgentMap.remove(extractionAgentMap.keySet().iterator().next());
        }
    }
}
