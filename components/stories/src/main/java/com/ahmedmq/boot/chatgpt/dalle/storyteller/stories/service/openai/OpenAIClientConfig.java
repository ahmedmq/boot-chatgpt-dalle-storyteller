package com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service.openai;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties(prefix = "openai")
public class OpenAIClientConfig {

    @Value("${openai.http-client.read-timeout}")
    private int readTimeout;

    @Value("${openai.http-client.connect-timeout}")
    private int connectTimeout;

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.gpt-model}")
    private String model;

    @Bean
    public Request.Options options() {
        return new Request.Options(connectTimeout, TimeUnit.SECONDS, readTimeout, TimeUnit.SECONDS, true);
    }

    @Bean
    public Logger.Level feignLogger() {
        return Logger.Level.FULL;
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default();
    }

    @Bean
    public RequestInterceptor apiKeyInterceptor() {
        return request -> request.header("Authorization", "Bearer " + apiKey);
    }

    public String getModel() {
        return model;
    }
}