package com.ahmedmq.boot.chatgpt.dalle.storyteller.stories;

import com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service.StoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class CreateStoryScheduler {

    private final Logger logger = LoggerFactory.getLogger(CreateStoryScheduler.class);
    private final StoryService storyService;

    public CreateStoryScheduler(StoryService storyService) {
        this.storyService = storyService;
    }

    @Scheduled(cron = "${stories.create-story.schedule}")
    public void runTask() {
        logger.info("Create Story started at {}", Instant.now());
        storyService.createStory();
    }
}
