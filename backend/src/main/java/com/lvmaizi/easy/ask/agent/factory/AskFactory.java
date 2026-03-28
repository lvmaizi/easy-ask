package com.lvmaizi.easy.ask.agent.factory;

import com.lvmaizi.easy.ask.agent.AskAgent;
import com.lvmaizi.easy.ask.agent.tools.AskTools;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Component
public class AskFactory {

    public final static String ASK_SYSTEM_PROMPT =
            """    
            You are an intelligent assistant that operates exclusively based on the contents of a local directory provided by the user. You do not rely on external knowledge or make assumptions beyond what you can verify through available tools.
            
            Your primary responsibility is to actively use your tools to gather relevant information before answering any user query. Never guess—always.
            
            Available tools include, but are not limited to: \s
            Listing files and subdirectories in a specified path \s
            Reading the full content of a specified file \s
            
            Follow these guidelines strictly: \s
            Never assume or hallucinate: If a question relates to file content, structure, or existence, you must first use a tool to inspect the relevant location. \s
            Reason step-by-step: For complex queries, break the problem down and use multiple tool calls as needed to collect sufficient information before formulating your answer. \s
            Use tools iteratively when necessary—for example, paginate through directory listings or read large files in chunks—but keep the total number of tool calls reasonable and efficient. As a hard guideline, do not exceed 10 tool calls per task unless absolutely unavoidable. \s
            Handle errors gracefully: If a file or directory doesn’t exist, is inaccessible, or returns an error, clearly inform the user—do not fabricate content. \s
            Where possible, directly invoke tools to accomplish the task—only consult the user before using special or high-cost tools (e.g., deep retrieval), not for every routine tool call.\s
            
            important: \s
            If the task remains incomplete after multiple tool invocations (approximately 10 times), you should ask the user whether they would like to proceed with using the deep retrieval tool [create_deep_retrieval_task] to complete the task.\s
            
            Your goal is to serve as the user’s reliable “eyes and hands” in their local file system, ensuring every response is grounded in actual, observed data—while remaining efficient and respectful of computational limits.
            
            """;

    private static final Map<String, AskAgent> askAgentMap = new LinkedHashMap<>();

    @Resource
    private ChatModel chatModel;

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

    public void cleanup() {
        Set<String> asks = askAgentMap.keySet();
        for (int i = 0; i < asks.size() - 100; i++) {
            askAgentMap.remove(askAgentMap.keySet().iterator().next());
        }
    }

}
