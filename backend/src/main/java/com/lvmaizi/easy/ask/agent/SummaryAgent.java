package com.lvmaizi.easy.ask.agent;

import org.springframework.ai.chat.client.ChatClient;

public class SummaryAgent {

    private final ChatClient chatClient;

    public SummaryAgent(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String run(String content) {
        if (content == null || content.isBlank()) {
            return "";
        }

        String userPrompt = """
                
                以下为文件内容：
                
                {%s}
                
                """.formatted(content);

        return chatClient.prompt(userPrompt).call().content();
    }

}
