package com.lvmaizi.easy.ask.agent.tools;

import com.lvmaizi.easy.ask.agent.reader.Reader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ReadFileChunk {

    private static final String Skill =
            """
            
            ## 文件内容如下, 当前页码:{page}
            {file_content}
            
            """;

    @Autowired
    private List<Reader> readerList;

    public ReadFileChunk(List<Reader> readerList) {
        this.readerList = readerList;
    }

    public String run(String path, int page) {
        File file = new File(path);
        if (!file.exists()) {
            log.warn("File does not exist: {}", path);
            return "文件不存在";
        }
        for (Reader reader : readerList) {
            if (reader.support(path)) {
                PromptTemplate promptTemplate = new PromptTemplate(Skill);
                Map<String, Object> model = new HashMap<>();
                String content = reader.read(path);

                int startIndex = (page - 1) * 5000;
                int endIndex = Math.min(startIndex + 5000, content.length());

                String pageContent = "";
                if (startIndex < content.length()) {
                    pageContent = content.substring(startIndex, endIndex);
                } else {
                    return  "文件已结束";
                }

                model.put("file_content", pageContent);
                model.put("page", page);
                return promptTemplate.render(model);
            }
        }
        return "null";
    }
}
