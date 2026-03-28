package com.lvmaizi.easy.ask.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.ToolResponseMessage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class MessagesLog {

    public static void log(List<Message> messages) {
        String content = messages.stream().map(
                        message -> {
                            switch (message.getMessageType()) {
                                case MessageType.USER -> {
                                    return  "用户：" + message.getText();
                                }
                                case MessageType.ASSISTANT -> {
                                    if (message instanceof AssistantMessage assistantMessage) {
                                        if (assistantMessage.hasToolCalls()) {
                                            return  "助手：请调用工具 " + assistantMessage.getToolCalls().getLast().name();
                                        }
                                    }
                                    return  "助手：" + message.getText();
                                }
                                case MessageType.SYSTEM -> {
                                    return  "系统：" + message.getText();
                                }
                                case MessageType.TOOL -> {
                                    if (message instanceof ToolResponseMessage toolResponseMessage) {
                                        return  "工具：工具返回" + toolResponseMessage.getResponses().getLast().responseData();
                                    }
                                    return "工具：" + message.getText();
                                }
                            };
                            return "";
                        })
                .collect(Collectors.joining("\n"));
        System.out.println("MessagesLog =============: \n" + content);
    }

}
