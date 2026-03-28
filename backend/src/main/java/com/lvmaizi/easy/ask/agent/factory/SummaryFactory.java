package com.lvmaizi.easy.ask.agent.factory;

import com.lvmaizi.easy.ask.agent.SummaryAgent;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

@Component
public class SummaryFactory {

    @Resource
    private ChatModel chatModel;

    private final static String SUMMARY_PROMPT = """
            
            You are a File Summary Assistant. Your sole task is to generate a concise, informative summary of a given file’s content. This summary will be used by other agents to quickly determine whether the file is likely to contain information relevant to a user’s question.
            
            Guidelines:
            Focus on key topics, entities, purposes, and conclusions in the file.
            Highlight what the file is about, not how it is formatted or structured (unless structure is semantically important, e.g., a config file with specific parameters).
            Keep the summary neutral, factual, and grounded strictly in the provided content—do not infer, assume, or add external knowledge.
            The summary should be concise and strictly limited to under 300 words—typically 1–3 sentences capturing the core content of the file.
            
            Important:
            You will receive the full or partial content of a single file.
            Your output will be used in downstream retrieval decisions—accuracy and signal density matter more than elegance.
            
            Now, generate a summary based on the file content provided.
            
            """;

    public SummaryAgent getSummaryAgent() {
        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultSystem(p -> p.text(SUMMARY_PROMPT))
                .build();

        return new SummaryAgent(chatClient);
    }
}
