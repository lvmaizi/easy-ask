package com.lvmaizi.easy.ask.service.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatMessageResponse {

    private String spaceId;

    private String role;

    private String content;
}
