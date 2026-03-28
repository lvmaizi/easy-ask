package com.lvmaizi.easy.ask.agent.factory;

import com.lvmaizi.easy.ask.agent.*;
import com.lvmaizi.easy.ask.utils.SpringBeanUtils;
import org.springframework.stereotype.Component;

@Component
public class AgentFactory {

    public static AskAgent getAskAgent(String sessionId) {
        return SpringBeanUtils.getBean(AskFactory.class).getAskAgent(sessionId);
    }

    public static KnowledgeAgent getExtractionAgent(String sessionId) {
        return SpringBeanUtils.getBean(ExtractionFactory.class).getExtractionAgent(sessionId);
    }

    public static void clear() {
        SpringBeanUtils.getBean(AskFactory.class).cleanup();
        SpringBeanUtils.getBean(ExtractionFactory.class).cleanup();
    }

    public static SummaryAgent getSummaryAgent() {
        return SpringBeanUtils.getBean(SummaryFactory.class).getSummaryAgent();
    }


    public static RetrievalAgent getDeepRetrievalAgent() {
        return SpringBeanUtils.getBean(RetrievalFactory.class).getDeepRetrievalAgent();
    }
    public static SimpleAgent getSimpleAgent() {
        return SpringBeanUtils.getBean(SimpleFactory.class).getSimpleAgent();
    }
}
