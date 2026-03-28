package com.lvmaizi.easy.ask.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

public class SimpleAgent {

    private final ChatClient chatClient;

    public SimpleAgent(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String run(String prompt, List<Message> messages) {
        if (messages.size() <= 2) {
            return "";
        }

        return  chatClient.prompt(prompt).messages(messages).call().content();
    }

}
