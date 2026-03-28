package com.lvmaizi.easy.ask.agent.compress;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.ToolResponseMessage;

import java.util.List;

public class Masking {

    public static List<Message> mask(List<Message> messages, int maxLength) {
        for (int i = 0; i < messages.size() - maxLength; i++) {
            Message message = messages.get(i);
            if (message.getMessageType() == MessageType.TOOL) {
                messages.set(i, mask(message));
            }
        }
        return messages;
    }

    private static Message mask(Message message) {

        if (message instanceof ToolResponseMessage toolResponseMessage) {
            ToolResponseMessage.ToolResponse last = toolResponseMessage.getResponses().getLast();

            return ToolResponseMessage.builder()
                    .responses(List.of(
                            new ToolResponseMessage.ToolResponse(last.id(), last.name(), "内容已省略...")))
                    .metadata(toolResponseMessage.getMetadata())
                    .build();
        }
        return message;
    }
}
