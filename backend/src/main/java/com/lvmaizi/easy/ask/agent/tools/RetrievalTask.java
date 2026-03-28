package com.lvmaizi.easy.ask.agent.tools;

import com.lvmaizi.easy.ask.agent.factory.AgentFactory;
import com.lvmaizi.easy.ask.agent.RetrievalAgent;
import com.lvmaizi.easy.ask.utils.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class RetrievalTask {

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public String run(String prompt) {
        log.info("AskTask:{}", prompt);
        executorService.submit(() -> {
            RetrievalAgent retrievalAgent = AgentFactory.getDeepRetrievalAgent();
            retrievalAgent.run(prompt);
            AgentFactory.getExtractionAgent(IdGenerator.id()).run(retrievalAgent.getMessages());
        });
        return "已经创建深度检索任务，可提醒用户稍后再来询问";
    }
}
