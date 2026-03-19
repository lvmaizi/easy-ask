package com.lvmaizi.easy.ask.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvmaizi.easy.ask.repository.MybatisMappers;
import com.lvmaizi.easy.ask.repository.entity.VectorEntity;
import com.lvmaizi.easy.ask.service.VectorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class VectorServiceImpl implements VectorService {
    @Override
    public String list() {
        String result = """
                <html>
                <body>
                <pre>
                
                向量知识库总数量:{%s}
                
                top10:
                %s
                
                </pre>
                </body>
                </html>
                """;
        BaseMapper<VectorEntity> mapper = MybatisMappers.getMapper(VectorEntity.class);
        Long count = mapper.selectCount(null);

        List<VectorEntity> top = mapper.selectList(new QueryWrapper<VectorEntity>().select("query", "content").orderByDesc("priority")
                .last("limit 10"));
        String top10 = top.stream().map(e -> e.getQuery() + " : " + e.getContent()).collect(Collectors.joining("\n"));

        return result.formatted(count, top10);
    }
}
