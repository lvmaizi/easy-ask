package com.lvmaizi.easy.ask.service.impl;

import cn.hutool.core.io.resource.ClassPathResource;
import com.lvmaizi.easy.ask.service.ScriptConfigService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
public class ScriptConfigServiceImpl implements ScriptConfigService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @PostConstruct
    @Override
    public void init() {
        createVecTable();
        createFileTable();
        log.info("Script config init completed");
    }

    private void createVecTable() {
        try {
            String path = getVecPath();
            ClassPathResource resource = new ClassPathResource(path);
            File tempFile = File.createTempFile("vec0_", ".dll");
            tempFile.deleteOnExit();
            Files.copy(resource.getStream(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            String absolutePath = tempFile.getAbsolutePath();
            this.jdbcTemplate.execute((ConnectionCallback<?>) con -> {
                try (var stmt = con.createStatement()) {
                    // 启用扩展加载
                    stmt.execute("PRAGMA enable_load_extension = 1");

                    // 加载 vec0 扩展
                    stmt.execute("SELECT load_extension('" + absolutePath + "')");

                    // 创建虚拟表
                    String createEmbeddingTable = """
                    CREATE VIRTUAL TABLE IF NOT EXISTS t_embeddings
                    USING vec0(
                        id INTEGER PRIMARY KEY,
                        priority INTEGER,
                        query TEXT,
                        content TEXT,
                        embedding FLOAT[1024]
                    )
                    """;
                    stmt.execute(createEmbeddingTable);
                } catch (Exception e) {
                    log.error("Error creating embedding table", e);
                }
                return null;
            });
        } catch (Exception e) {
            log.error("Error creating file table", e);
        }

    }

    private void createFileTable() {
        String createDocTable = """
                CREATE TABLE IF NOT EXISTS t_file (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    path TEXT,
                    name TEXT,
                    last_modified INTEGER,
                    length INTEGER
                )
                """;
        this.jdbcTemplate.execute(createDocTable);
    }

    private String getVecPath() {

        String osName = System.getProperty("os.name").toLowerCase();

        String path = null;
        if (osName.contains("win")) {
            path = "native/vec0.dll";
        } else if (osName.contains("mac")) {
            path = "native/vec0.dylib";
        } else if (osName.contains("linux")) {
            path = "native/vec0.so";
        } else if (osName.contains("unix")) {
            path = "native/vec0.so";
        }
        return path;
    }
}
