package com.lvmaizi.easy.ask.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvmaizi.easy.ask.agent.AgentFactory;
import com.lvmaizi.easy.ask.agent.SummaryAgent;
import com.lvmaizi.easy.ask.agent.tools.ReadFileChunk;
import com.lvmaizi.easy.ask.repository.MybatisMappers;
import com.lvmaizi.easy.ask.repository.SimpleUpdate;
import com.lvmaizi.easy.ask.repository.entity.FileEntity;
import com.lvmaizi.easy.ask.service.FileSummaryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FileSummaryServiceImpl implements FileSummaryService {

    @Resource
    private AgentFactory agentFactory;

    @Resource
    private ReadFileChunk readFileChunk;

    @Override
    @Scheduled(fixedRate = 300000)
    public void run() {
        log.info("FileSummaryServiceImpl run");
        doRun();
    }

    public void doRun() {

        SummaryAgent summaryAgent = agentFactory.getSummaryAgent();

        BaseMapper<FileEntity> mapper = MybatisMappers.getMapper(FileEntity.class);
        QueryWrapper<FileEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(qw -> qw.isNull("summary").or().eq("summary", "").or().eq("summary", " "))
                .last("limit 10");

        List<FileEntity> list = mapper.selectList(queryWrapper);
        for (FileEntity fileEntity : list) {
            log.info("fileEntity: {}", fileEntity);
            String content = readFileChunk.run(fileEntity.getPath(), 1);
            String summary = summaryAgent.run(content);
            fileEntity.setSummary(summary);
            SimpleUpdate.update(Map.of("path",  fileEntity.getPath()), fileEntity);
        }
    }
}
