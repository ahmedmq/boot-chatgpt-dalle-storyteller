package com.ahmedmq.boot.chatgpt.dalle.storyteller.images;

import com.ahmedmq.boot.chatgpt.dalle.storyteller.images.service.ImageService;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.image.ImageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CreateStoryImageFunction implements Function<StoryEvent, Message<ImageResult>> {
    Logger logger = LoggerFactory.getLogger(CreateStoryImageFunction.class);

    private final ImageService imageService;

    public CreateStoryImageFunction(ImageService imageService) {
        this.imageService = imageService;
    }


    @Override
    public Message<ImageResult> apply(StoryEvent storyEvent) {
        logger.info("Received: {}", storyEvent);
        ImageResult imageResult = imageService.createImage(storyEvent.getScene().toString());
        return MessageBuilder.withPayload(imageResult)
                .setHeader("X-STORY-ID", storyEvent.getId())
                .build();
    }
}
