package com.lvmaizi.easy.ask.agent.compress;

import com.lvmaizi.easy.ask.agent.factory.AgentFactory;
import com.lvmaizi.easy.ask.agent.SimpleAgent;
import com.lvmaizi.easy.ask.utils.SpringBeanUtils;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.List;

public class Summarization {

    public static List<Message> summarize(List<Message> messages, int roundsToRetain) {
        // 保留当前一轮对话，摘要历史对话至当前一轮对话
        int currentRound = -1;
        int countRound = 0;
        for (int i = messages.size() - 1; i >= 0; i--) {
            if (messages.get(i).getMessageType() == MessageType.USER) {
                countRound ++;
                if (countRound == roundsToRetain) {
                    currentRound = i;
                    break;
                }
            }
        }

        if (currentRound > 0) {
            List<Message> history = messages.subList(0, currentRound);
            String summary = summarizeText(history);
            List<Message> currentRoundMessages = messages.subList(currentRound - 1, messages.size());
            currentRoundMessages.set(0, new UserMessage("""
                    历史会话摘要：%s
                    
                    当前用户输入：%s
                    """.formatted(summary, currentRoundMessages.getFirst().getText())));
            return currentRoundMessages;
        }
        return messages;
    }

    private static String summarizeText(List<Message> messages) {
        SimpleAgent simpleAgent = AgentFactory.getSimpleAgent();
        String prompt = """
                You are a helpful assistant that summarizes the conversation.
                """;
        return simpleAgent.run(prompt, messages);
    }
}
