package com.lvmaizi.easy.ask.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvmaizi.easy.ask.agent.dataset.SupportFiles;
import com.lvmaizi.easy.ask.repository.MybatisMappers;
import com.lvmaizi.easy.ask.repository.SimpleInsert;
import com.lvmaizi.easy.ask.repository.SimpleQuery;
import com.lvmaizi.easy.ask.repository.entity.FileEntity;
import com.lvmaizi.easy.ask.service.FileScanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileScanServiceImpl implements FileScanService, ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Thread(this::scan, "File Scan Task").start();
    }

    @Override
    @Scheduled(fixedRate = 300000)
    public synchronized void scan() {
        doScan();
        BaseMapper<FileEntity> mapper = MybatisMappers.getMapper(FileEntity.class);
        Long count = mapper.selectCount(null);
        log.info("File scan completed, total: {}", count);
    }

    @Override
    public String list() {
        String result = """
                <html>
                <body>
                <pre>
                文件总数量:{%s}
                
                top100:
                %s
                
                </pre>
                </body>
                </html>
                """;
        BaseMapper<FileEntity> mapper = MybatisMappers.getMapper(FileEntity.class);
        Long count = mapper.selectCount(null);

        List<FileEntity> top = mapper.selectList(new QueryWrapper<FileEntity>().orderByAsc("last_modified").last("limit 100"));
        String top10 = top.stream().map(
                doc -> doc.getPath() + "\n" + doc.getSummary()
        ).collect(Collectors.joining("\n"));

        return result.formatted(count, top10);
    }

    private void doScan() {
        String basePath = System.getProperty("basePath");
        if (basePath == null || basePath.trim().isEmpty()) {
            log.warn("doc.base.path not configured, skipping file scan");
            return;
        }

        File baseDir = new File(basePath);
        if (!baseDir.exists()) {
            log.warn("Base path does not exist: {}, skipping file scan", basePath);
            return;
        }

        scanInsert(baseDir);
        scanDelete();
    }

    private void scanDelete() {
        String basePath = System.getProperty("basePath");

        List<FileEntity> allDocs = SimpleQuery.list(Map.of(), FileEntity.class);
        if (allDocs == null || allDocs.isEmpty()) {
            return;
        }

        for (FileEntity doc : allDocs) {
            File file = new File(doc.getPath());
            if (!file.exists() || !file.getAbsolutePath().contains(basePath)) {
                BaseMapper<FileEntity> mapper = MybatisMappers.getMapper(FileEntity.class);
                mapper.deleteByMap(Map.of("path", doc.getPath()));
                log.debug("Deleted non-existent document record: {}, path: {}", doc.getName(), doc.getPath());
            }
        }
    }

    private void scanInsert(File dir) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            // Skip hidden files and .easy-ask directory
            if (file.isHidden() || file.getName().startsWith(".")) {
                continue;
            }

            if (file.isDirectory()) {
                scanInsert(file);
            } else {
                if (!SupportFiles.support(file.getAbsolutePath())) {
                    continue;
                }
                FileEntity doc = new FileEntity();
                doc.setName(file.getName());
                doc.setPath(file.getAbsolutePath());
                doc.setLastModified(new Date(file.lastModified()));
                doc.setLength(file.length());
                FileEntity path = SimpleQuery.getOne(Map.of("path", doc.getPath()), FileEntity.class);
                if (path == null) {
                    SimpleInsert.save(doc);
                }
            }
        }
    }
}
