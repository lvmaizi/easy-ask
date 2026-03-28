package com.lvmaizi.easy.ask.agent.factory;

import com.lvmaizi.easy.ask.agent.SimpleAgent;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

@Component
public class SimpleFactory {

    @Resource
    private ChatModel chatModel;

    private final static String SIMPLE_PROMPT = """
            
            You are a Assistant.
            
            """;

    public SimpleAgent getSimpleAgent() {
        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultSystem(p -> p.text(SIMPLE_PROMPT))
                .build();

        return new SimpleAgent(chatClient);
    }
}
