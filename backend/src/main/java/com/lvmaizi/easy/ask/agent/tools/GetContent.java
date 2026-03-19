package com.lvmaizi.easy.ask.agent.tools;

import com.lvmaizi.easy.ask.agent.reader.Reader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GetContent {

    private static final String Skill =
            """
            
            ## 文件内容如下
            {file_content}
            
            """;

    @Autowired
    private List<Reader> readerList;

    public GetContent(List<Reader> readerList) {
        this.readerList = readerList;
    }

    public String run(String path) {
        for (Reader reader : readerList) {
            if (reader.support(path)) {
                PromptTemplate promptTemplate = new PromptTemplate(Skill);
                Map<String, Object> model = new HashMap<>();
                model.put("file_content", reader.read(path));
                return promptTemplate.render(model);
            }
        }
        return "null";
    }
}
