package com.lvmaizi.easy.ask.service.impl;

import com.lvmaizi.easy.ask.agent.factory.AgentFactory;
import com.lvmaizi.easy.ask.service.AgentClearService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class AgentClearServiceImpl implements AgentClearService {
    @Override
    @Scheduled(fixedRate = 300000)
    public void clear() {
        AgentFactory.clear();
    }
}
