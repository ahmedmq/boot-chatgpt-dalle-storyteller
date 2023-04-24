package com.ahmedmq.boot.chatgpt.dalle.storyteller.openai;

import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.chat.ChatCompletionRequest;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.chat.ChatCompletionResponse;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.image.CreateImageRequest;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.image.ImageResult;
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

    @PostMapping(value = "${openai.urls.image-url}", headers = {"Content-Type=application/json"})
    ImageResult createImage(@RequestBody CreateImageRequest createImageRequest);


}
