package com.lvmaizi.easy.ask.service.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatCompletionRequest {

    private String sessionId;

    private String prompt;
}
