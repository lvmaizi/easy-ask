package com.lvmaizi.easy.ask.config;

import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class EmbeddingConfig {


    @Value("${embedding.model.url}")
    private String baseUrl;

    @Value("${embedding.model.apiKey}")
    private String apiKey;

    @Value("${embedding.model.name}")
    private String name;

    @Bean
    public OpenAiEmbeddingModel embeddingModel() {
        OpenAiEmbeddingOptions options = OpenAiEmbeddingOptions.builder()
                .model(name)
                .build();

        RestClient.Builder restClientBuilder = RestClient.builder();
        restClientBuilder.requestInterceptor(new LoggingInterceptor());

        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .restClientBuilder(restClientBuilder)
                .build();
        return new OpenAiEmbeddingModel(openAiApi, MetadataMode.EMBED, options);
    }

}
