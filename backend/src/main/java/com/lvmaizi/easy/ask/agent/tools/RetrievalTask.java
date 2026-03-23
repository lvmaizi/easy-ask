package com.lvmaizi.easy.ask.agent.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RetrievalTask {

    public String run(String prompt) {
        log.info("AskTask:{}", prompt);
        return "已经创建深度检索任务，可提醒用户稍后再来询问";
    }
}
