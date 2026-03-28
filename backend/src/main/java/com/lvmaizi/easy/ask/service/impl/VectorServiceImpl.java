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

        StringBuilder top10 = new StringBuilder();

        for (int i = 0; i < top.size(); i++) {
            VectorEntity e = top.get(i);
            top10.append("======== ").append(i + 1).append(". ").append(e.getQuery()).append("\n") ;
            top10.append(">>>>>>>>").append(e.getContent()).append("\n\n") ;
        }

        return result.formatted(count, top10.toString());
    }
}
