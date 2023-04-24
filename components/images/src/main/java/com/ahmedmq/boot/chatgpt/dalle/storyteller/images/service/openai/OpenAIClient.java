package com.ahmedmq.boot.chatgpt.dalle.storyteller.images.service.openai;

import com.ahmedmq.boot.chatgpt.dalle.storyteller.images.service.openai.model.CreateImageRequest;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.images.service.openai.model.ImageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "openai-service",
        url = "${openai.urls.base-url}",
        configuration = OpenAIClientConfig.class
)

public interface OpenAIClient {
    @PostMapping(value = "${openai.urls.image-url}", headers = {"Content-Type=application/json"})
    ImageResult createImage(@RequestBody CreateImageRequest createImageRequest);
}
