package com.ahmedmq.boot.chatgpt.dalle.storyteller.images;

import com.ahmedmq.boot.chatgpt.dalle.storyteller.images.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class StoryEventConsumer implements Consumer<StoryEvent> {
    Logger logger = LoggerFactory.getLogger(StoryEventConsumer.class);

    private final ImageService imageService;

    public StoryEventConsumer(ImageService imageService) {
        this.imageService = imageService;
    }

    @Override
    public void accept(StoryEvent storyMessage) {
        logger.info("Received: {}", storyMessage);
        imageService.createImage(storyMessage.getScene().toString());

    }
}
