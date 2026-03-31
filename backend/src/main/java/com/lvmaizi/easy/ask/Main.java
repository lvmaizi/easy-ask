package com.lvmaizi.easy.ask;

import com.lvmaizi.easy.ask.service.impl.EnvConfigServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.servlet.context.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.lvmaizi"})
@ServletComponentScan
@EnableScheduling
@EnableAsync
@Slf4j
public class Main {
    public static void main(String[] args) throws Exception {
        new EnvConfigServiceImpl().init();
        SpringApplication.run(Main.class, args);
        String basePath = System.getProperty("base.path");
        log.info("App base path：{}", basePath);
    }

}