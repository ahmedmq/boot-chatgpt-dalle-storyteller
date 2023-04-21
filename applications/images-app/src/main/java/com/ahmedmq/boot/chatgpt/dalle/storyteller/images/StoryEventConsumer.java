package com.ahmedmq.boot.chatgpt.dalle.storyteller.images;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class StoryEventConsumer implements Consumer<StoryEvent> {

    Logger logger = LoggerFactory.getLogger(StoryEventConsumer.class);

    @Override
    public void accept(StoryEvent storyMessage) {
        logger.info("Received: {}", storyMessage);
    }
}
