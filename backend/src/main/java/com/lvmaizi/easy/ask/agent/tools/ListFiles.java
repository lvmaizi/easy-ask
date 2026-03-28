package com.lvmaizi.easy.ask.agent.tools;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvmaizi.easy.ask.repository.MybatisMappers;
import com.lvmaizi.easy.ask.repository.entity.FileEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ListFiles {

    private static final String Skill =
            """
            
            ## 获取的文件列表如下（当前页码：{page}，你可累加页码继续查询后续文件，直到查询返回为空），
            
            {file_list}
            
            """;
    public String run(int page) {

        BaseMapper<FileEntity> mapper = MybatisMappers.getMapper(FileEntity.class);

        QueryWrapper<FileEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.last("limit " + (page - 1) * 100 + ", 100");
        List<FileEntity> list = mapper.selectList(queryWrapper);
        return renderWithPromptTemplate(page, list);
    }

    private String renderWithPromptTemplate(int page, List<FileEntity> files) {
        PromptTemplate promptTemplate = new PromptTemplate(Skill);

        // 在 Java 中处理列表
        String fileListText = files.stream()
                .map(file -> "- " + file.getName() + ": " + file.getPath() + "\n该文件摘要为：" + StringUtils.defaultString(file.getSummary()))
                .collect(Collectors.joining("\n"));

        Map<String, Object> model = new HashMap<>();
        model.put("page", page);
        model.put("file_list", fileListText);  // 传入处理后的字符串

        return promptTemplate.render(model);
    }
}
