package com.lvmaizi.easy.ask.agent;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lvmaizi.easy.ask.repository.SimpleInsert;
import com.lvmaizi.easy.ask.repository.entity.VectorEntity;
import com.lvmaizi.easy.ask.utils.BytesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;

import java.util.List;
import java.util.stream.Collectors;

public class ExtractionAgent {

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;

    public ExtractionAgent(ChatClient chatClient, EmbeddingModel embeddingModel) {
        this.chatClient = chatClient;
        this.embeddingModel = embeddingModel;
    }

    public void run(List<Message> messages) {
        if (messages.size() <= 2) {
            return;
        }

        String content = messages.stream().map(
                message -> message.getMessageType().name() + " : " + message.getText())
                .collect(Collectors.joining("\n"));

        String userPrompt = """
                
                以下为聊天记录：
                
                {%s}
                
                """.formatted(content);

        String result = chatClient.prompt(userPrompt).call().content();
        resolve(result);
    }

    private void resolve(String result) {

        JSONArray jsonArray = JSONUtil.parseArray(result);
        if (jsonArray.isEmpty()) {
            return;
        }

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String query = jsonObject.getStr("query");
            String answer = jsonObject.getStr("answer");
            if (StringUtils.isBlank(query) || StringUtils.isBlank(answer)) {
                continue;
            }
            EmbeddingRequest request = new EmbeddingRequest(List.of(query), null);
            Embedding embedding = embeddingModel.call(request).getResult();

            VectorEntity vectorEntity = new VectorEntity();
            vectorEntity.setQuery(query);
            vectorEntity.setContent(answer);
            vectorEntity.setPriority(0);
            vectorEntity.setEmbedding(BytesUtil.floatArrayToByteArray(embedding.getOutput()));
            SimpleInsert.save(vectorEntity);
        }
    }

}
