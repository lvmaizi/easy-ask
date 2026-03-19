package com.lvmaizi.easy.ask.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class SpringAIConfig {

    @Value("${model.url}")
    private String baseUrl;

    @Value("${model.apiKey}")
    private String apiKey;

    @Value("${model.name}")
    private String name;

    @Bean
    public ChatModel chatModel() {
        RestClient.Builder restClientBuilder = RestClient.builder();
        restClientBuilder.requestInterceptor(new LoggingInterceptor());

        MyChatModel.Builder builder = MyChatModel.builder();
        builder.defaultOptions(OpenAiChatOptions.builder().model(name).internalToolExecutionEnabled(false).build());
        builder.openAiApi(OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .restClientBuilder(restClientBuilder)
                .build());
        return builder.build();
    }
}
