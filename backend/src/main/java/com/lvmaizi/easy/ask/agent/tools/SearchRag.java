package com.lvmaizi.easy.ask.agent.tools;

import com.lvmaizi.easy.ask.agent.reader.Reader;
import com.lvmaizi.easy.ask.repository.MybatisMappers;
import com.lvmaizi.easy.ask.repository.entity.VectorEntity;
import com.lvmaizi.easy.ask.repository.mapper.VectorMapper;
import com.lvmaizi.easy.ask.utils.BytesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SearchRag {

    @Autowired
    private VectorMapper vectorMapper;

    @Autowired
    private OpenAiEmbeddingModel embeddingModel;

    private static final String Skill =
            """
            
            ## 检索到该问题的常见问答如下（本Rag库若未检索到请立即使用其他工具完成任务）:
            
            {content}
            
           
            """;

    @Autowired
    private List<Reader> readerList;

    public String run(String prompt) {
        EmbeddingRequest request = new EmbeddingRequest(List.of(prompt), null);
        EmbeddingResponse response = embeddingModel.call(request);
        Embedding embedding = response.getResult();
        float[] output = embedding.getOutput();
        byte[] bytes = BytesUtil.floatArrayToByteArray(output);
        List<VectorEntity> vectors = vectorMapper.search(bytes, 2);

        adjust(vectors);

        PromptTemplate promptTemplate = new PromptTemplate(Skill);
        Map<String, Object> model = new HashMap<>();
        String content = vectors.stream().map(e -> e.getQuery() + " : " + e.getContent()).collect(Collectors.joining("\n"));
        if (StringUtils.isBlank( content)) {
            content = "无";
        }
        model.put("content", content);
        return promptTemplate.render(model);
    }

    @Async
    private void adjust(List<VectorEntity> vectors) {
        for (VectorEntity vector : vectors) {
            VectorEntity update = new VectorEntity();
            update.setId(vector.getId());
            update.setPriority(vector.getPriority() + 1);
            MybatisMappers.getMapper(VectorEntity.class).updateById(update);
        }
    }
}
