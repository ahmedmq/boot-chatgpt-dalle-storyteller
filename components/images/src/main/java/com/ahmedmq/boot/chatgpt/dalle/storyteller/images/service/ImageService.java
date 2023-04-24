package com.ahmedmq.boot.chatgpt.dalle.storyteller.images.service;


import com.ahmedmq.boot.chatgpt.dalle.storyteller.images.service.openai.OpenAIClient;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.images.service.openai.OpenAIClientConfig;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.images.service.openai.model.CreateImageRequest;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.images.service.openai.model.ImageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;


@Service
@EnableFeignClients(basePackageClasses = OpenAIClient.class)
@ComponentScan(basePackageClasses = OpenAIClientConfig.class)
public class ImageService {

    Logger logger = LoggerFactory.getLogger(ImageService.class);

    private final OpenAIClient openAIClient;

    public ImageService(OpenAIClient openAIClient) {
        this.openAIClient = openAIClient;
    }

    public ImageResult createImage(String prompt) {
        ImageResult imageResult = openAIClient.createImage(new CreateImageRequest(prompt));
        logger.info("Image URL: {}", imageResult.data().get(0).url());

        return imageResult;

    }
}
