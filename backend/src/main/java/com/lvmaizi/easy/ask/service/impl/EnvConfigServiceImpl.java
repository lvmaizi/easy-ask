package com.lvmaizi.easy.ask.service.impl;

import com.lvmaizi.easy.ask.service.EnvConfigService;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

@Slf4j
public class EnvConfigServiceImpl implements EnvConfigService {
    public  void init()  {
        initBasePath();
        initEasyAskPath();
        initDatabase();
    }

    private void initBasePath() {
        String basePath = System.getProperty("basePath");
        if (basePath == null || basePath.trim().isEmpty()) {
            basePath = System.getProperty("user.dir");
            System.setProperty("basePath", basePath);
        }
        log.info("App base path：{}", basePath);
    }


    private void initEasyAskPath() {
        String path = System.getProperty("basePath");
        File baseDir = new File(path);

        // 检查并创建基础路径目录
        if (!baseDir.exists()) {
            boolean created = baseDir.mkdirs();
            if (!created) {
                throw new RuntimeException("Failed to create base directory: " + path);
            }
        }

        // 在基础目录下创建 .easy-ask 文件夹
        File easyAskDir = new File(baseDir, ".easy-ask");
        if (!easyAskDir.exists()) {
            boolean created = easyAskDir.mkdirs();
            if (!created) {
                throw new RuntimeException("Failed to create .easy-ask directory: " + easyAskDir.getAbsolutePath());
            }
        }
    }

    private void initDatabase() {
        String path = System.getProperty("basePath");

        // 构建数据库文件路径
        String dbPath = new File(new File(path, ".easy-ask"), "easy-ask.db").getAbsolutePath();
        File dbFile = new File(dbPath);

        // 如果数据库文件不存在，则创建
        if (!dbFile.exists()) {
            try {
                boolean created = dbFile.createNewFile();
                if (!created) {
                    throw new RuntimeException("Failed to create database file.");
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to create database file.", e);
            }
        }

        // 更新 Spring 配置中的数据源 URL
        String databaseUrl = "jdbc:sqlite:" + dbPath + "?enable_load_extension=true";
        System.setProperty("spring.datasource.url", databaseUrl);
        log.info("Database URL: {}", databaseUrl);
    }
}
