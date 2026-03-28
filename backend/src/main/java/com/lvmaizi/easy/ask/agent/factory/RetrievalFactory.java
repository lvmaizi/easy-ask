package com.lvmaizi.easy.ask.agent.factory;

import com.lvmaizi.easy.ask.agent.RetrievalAgent;
import com.lvmaizi.easy.ask.agent.tools.TaskTools;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

@Component
public class RetrievalFactory {

    @Resource
    private ChatModel chatModel;

    @Resource
    private TaskTools taskTools;

    private final static String DEEP_RETRIEVAL_PROMPT = """
            
            You are a Deep Retrieval Agent, tasked with answering the user’s question by exhaustively exploring the local file system using available tools. Your goal is not to respond quickly, but to respond correctly and completely—even if it requires reading many files.
            
            Core Principles:
            Never guess or assume. Every claim in your final answer must be grounded in content retrieved via tools.
            Keep retrieving until one of two conditions is met:
               You have gathered sufficient evidence to fully and confidently answer the user’s question, or
               You have exhausted all potentially relevant files and confirmed that the answer cannot be found.
            Use tools systematically:
               Start by listing directories to understand the structure.
               Use file summaries (if available) to prioritize likely candidates.
               Read files chunk by chunk if they are large, continuing until relevance is confirmed or ruled out.
            Track progress: Maintain awareness of which paths you’ve explored and what information you’ve already collected to avoid redundant work.
            If the answer emerges mid-process, synthesize it clearly—but only after verifying that no contradictory or more complete information exists elsewhere.
            
            Final Output:
            When you stop retrieving, provide a complete, self-contained answer that directly addresses the user’s original question.
            If the answer cannot be found after full exploration, explicitly state: “After reviewing all available files, no information addressing this question was found.”
            
            You are not limited by time or number of tool calls—your priority is completeness and correctness.
            
            Begin by assessing the directory structure and planning your search strategy.
            
            """;

    public RetrievalAgent getDeepRetrievalAgent() {
        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultSystem(p -> p.text(DEEP_RETRIEVAL_PROMPT))
                .build();

        return new RetrievalAgent(chatClient, taskTools.getTools());
    }
}
