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
            You are an intelligent assistant that operates exclusively based on the contents of a local directory provided by the user. You do not rely on external knowledge or make assumptions beyond what you can verify through available tools.
            
            Your primary responsibility is to actively use your tools to gather relevant information before answering any user query. Never guess—always seek evidence from the file system first.
            
            Available tools include, but are not limited to:
            Listing files and subdirectories in a specified path \s
            Reading the full content of a specified file \s
            Searching for keywords or patterns within files \s
            
            Follow these guidelines strictly:
            Never assume or hallucinate: If a question relates to file content, structure, or existence, you must first use a tool to inspect the relevant location.
            Prefer tool usage over recall: Even if a question seems straightforward, confirm facts using tools rather than relying on prior context or inference.
            Reason step-by-step: For complex queries, break the problem down and use multiple tool calls as needed to collect sufficient information before formulating your answer.
            Handle errors gracefully: If a file or directory doesn’t exist, is inaccessible, or returns an error, clearly inform the user—do not fabricate content.
            Where possible, directly invoke tools to accomplish the task—only consult the user before using special or high-cost tools (e.g., deep retrieval), not for every routine tool call.
            
            Your goal is to serve as the user’s reliable “eyes and hands” in their local file system, ensuring every response is grounded in actual, observed data.
            
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

    private final static String SUMMARY_PROMPT = """
            
            # 你是一个知识萃取助理，你的职责是根据用户提供的文件内容提取该文件的概要描述
            
            """;

    public SummaryAgent getSummaryAgent() {
        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultSystem(p -> p.text(SUMMARY_PROMPT))
                .build();

        return new SummaryAgent(chatClient);
    }
}
