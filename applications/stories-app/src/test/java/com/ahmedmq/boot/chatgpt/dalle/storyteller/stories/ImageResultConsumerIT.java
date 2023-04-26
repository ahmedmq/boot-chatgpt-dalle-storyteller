package com.ahmedmq.boot.chatgpt.dalle.storyteller.stories;

import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.image.Image;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.image.ImageResult;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service.Story;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service.StoryDataGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
@Testcontainers
@ActiveProfiles("test")
class ImageResultConsumerIT {

    @Autowired
    InputDestination inputDestination;

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer();

    @Autowired
    StoryDataGateway storyDataGateway;

    @DynamicPropertySource
    static void register(DynamicPropertyRegistry registry){
        registry.add("spring.cloud.stream.kafka.binder.brokers", kafkaContainer::getBootstrapServers);
        registry.add("spring.datasource.url", ()-> "jdbc:postgresql://localhost:5450/stories-database");
        registry.add("spring.datasource.username", ()->"stories");
        registry.add("spring.datasource.password", ()->"password");
    }

    @Test
    void testFlow() {

        Long savedStoryId = storyDataGateway.saveStory("title", "description", new String[]{"new"}, "scene");

        Optional<Story> story = storyDataGateway.getStoryById(savedStoryId);

        assertThat(story).isNotEmpty();
        assertThat(story.get().url()).isNull();

        ImageResult imageResult = new ImageResult(
                Instant.now().toEpochMilli(),
                Collections.singletonList(new Image("www.example.com"))
        );
        inputDestination.send(
                MessageBuilder
                        .withPayload(imageResult)
                        .setHeader("X-STORY-ID", savedStoryId)
                        .build(),
                "stories.images");

        await().atMost(10, TimeUnit.SECONDS).until(urlUpdated(savedStoryId));

    }

    private Callable<Boolean> urlUpdated(Long id) {
        return () -> {
            Optional<Story> storyById = storyDataGateway.getStoryById(id);
            assertThat(storyById).isNotEmpty();
            return storyById.get().url().equals("www.example.com");
        };
    }
}