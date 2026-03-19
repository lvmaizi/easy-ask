package com.lvmaizi.easy.ask.service;

import com.lvmaizi.easy.ask.service.request.ChatCompletionRequest;
import com.lvmaizi.easy.ask.service.request.ChatCreateRequest;
import com.lvmaizi.easy.ask.service.response.BaseResponse;
import com.lvmaizi.easy.ask.service.response.ChatCreateResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public interface AssistantService {

    @PostMapping("/v1/assistant/chat/create")
    BaseResponse<ChatCreateResponse> createChat(@RequestBody ChatCreateRequest request);

    @PostMapping("/v1/assistant/chat/completion/stream")
    SseEmitter chatCompletion(@RequestBody ChatCompletionRequest completionRequest);

    @GetMapping("/v1/assistant/chat/history/{sessionId}")
    String getChatHistory(@PathVariable("sessionId") String sessionId);

}
