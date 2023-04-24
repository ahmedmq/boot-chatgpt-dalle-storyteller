package com.ahmedmq.boot.chatgpt.dalle.storyteller.stories;

import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.image.ImageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class ImageResultConsumer implements Consumer<Message<ImageResult>> {

    Logger logger = LoggerFactory.getLogger(ImageResultConsumer.class);

    @Override
    public void accept(Message<ImageResult> imageResultMessage) {

        Long storyId = (Long) imageResultMessage.getHeaders().get("X-STORY-ID");
        logger.info("Received image for story id: {}", storyId);
        logger.info("Received: {}", imageResultMessage.getPayload());

    }
}
