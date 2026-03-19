package com.lvmaizi.easy.ask.service.impl;

import com.lvmaizi.easy.ask.service.RagClearService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RagClearServiceImpl implements RagClearService {
    @Override
    @Scheduled(fixedRate = 300000)
    public void clear() {

    }
}
