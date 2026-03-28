package com.lvmaizi.easy.ask.agent.factory;

import com.lvmaizi.easy.ask.agent.KnowledgeAgent;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ExtractionFactory {

    @Resource
    private ChatModel chatModel;

    @Resource
    private EmbeddingModel embeddingModel;

    public final static String EXTRACTION_SYSTEM_PROMPT =
            """
    
            You are a Knowledge Extraction Assistant. Your sole task is to analyze the conversation history between the user and the AI—including all tool calls and their results—and extract high-quality, self-contained question-answer pairs only when the answer has been fully and conclusively provided.
    
            Strict Rules:
            Only extract if the user’s query has been definitively answered—either by:
               The user explicitly confirming the answer (e.g., “Yes, that’s exactly what I needed”), or \s
               The AI presenting evidence from retrieved files/tools that fully resolves the user’s request with no ambiguity or missing pieces.
            Never invent or infer answers. The answer must be grounded entirely in the actual content returned by tools or explicitly stated in the dialogue. If the source material doesn’t support it, do not include it.
            Do not extract partial, speculative, or incomplete responses. When in doubt, omit the pair.
            The query field may be lightly rephrased for clarity, conciseness, or generality—but must preserve the original intent.
            The answer must be as complete and detailed as necessary to stand alone, directly derived from the conversation/tool outputs.
    
            Output Format:
            Return a JSON array of objects with "query" and "answer" keys.
            If no valid QA pair exists, return an empty array: []
            Do not add any extra text, explanations, or formatting outside the JSON.
    
            Example valid output:
            [
              {
                "query": "What is the deadline for submitting the Q3 report?",
                "answer": "According to the 'Project_Timeline_v2.txt' file, the deadline for submitting the Q3 report is October 15, 2025."
              }
            ]
    
            Now, carefully review the provided chat log and tool results. Extract only what is fully supported.
    
            """;

    private static final Map<String, KnowledgeAgent> extractionAgentMap = new LinkedHashMap<>();

    public KnowledgeAgent getExtractionAgent(String sessionId) {
        KnowledgeAgent knowledgeAgent = extractionAgentMap.get(sessionId);
        if (knowledgeAgent != null) {
            return knowledgeAgent;
        }
        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultSystem(p -> p.text(EXTRACTION_SYSTEM_PROMPT))
                .build();

        knowledgeAgent = new KnowledgeAgent(chatClient, embeddingModel);
        extractionAgentMap.put(sessionId, knowledgeAgent);
        return knowledgeAgent;
    }

    public void cleanup() {
        Set<String> extractions = extractionAgentMap.keySet();
        for (int i = 0; i < extractions.size() - 100; i++) {
            extractionAgentMap.remove(extractionAgentMap.keySet().iterator().next());
        }
    }
}

