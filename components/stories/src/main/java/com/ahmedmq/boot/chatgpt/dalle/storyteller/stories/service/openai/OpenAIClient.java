package com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service.openai;

import com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service.openai.model.ChatCompletionRequest;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service.openai.model.ChatCompletionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "openai-service",
        url = "${openai.urls.base-url}",
        configuration = OpenAIClientConfig.class
)

public interface OpenAIClient {
    @PostMapping(value = "${openai.urls.chat-url}", headers = {"Content-Type=application/json"})
    ChatCompletionResponse chat(@RequestBody ChatCompletionRequest chatCompletionRequest);
}
