package com.ahmedmq.boot.chatgpt.dalle.storyteller.stories;

import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.image.Image;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.image.ImageResult;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service.Story;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service.StoryDataGateway;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service.StoryService;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class CreateStoryFlowIT {

    @Autowired
    InputDestination inputDestination;

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer();

    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14-bullseye")
            .withDatabaseName("stories-database")
            .withUsername("stories")
            .withPassword("password");

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension
            .newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @Autowired
    StoryDataGateway storyDataGateway;

    @Autowired
    StoryService storyService;

    @DynamicPropertySource
    static void register(DynamicPropertyRegistry registry){
        registry.add("spring.cloud.stream.kafka.binder.brokers", kafkaContainer::getBootstrapServers);
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password",postgreSQLContainer::getPassword);
        registry.add("openai.urls.base-url", wireMockServer::baseUrl);
    }

    @Test
    void testFlow() {

        wireMockServer.stubFor(
                post("/chat/completions")
                        .withHeader("Authorization", equalTo("Bearer abc"))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBodyFile("chat-completion-response.json")
                        )
        );

        Long storyId = storyService.createStory();
        assertThat(storyId).isNotNull();

        ImageResult imageResult = new ImageResult(
                Instant.now().toEpochMilli(),
                Collections.singletonList(new Image("www.example.com"))
        );
        inputDestination.send(
                MessageBuilder
                        .withPayload(imageResult)
                        .setHeader("X-STORY-ID", storyId)
                        .build(),
                "stories.images");

        await().atMost(10, TimeUnit.SECONDS).until(urlUpdated(storyId));

    }

    private Callable<Boolean> urlUpdated(Long id) {
        return () -> {
            Optional<Story> storyById = storyDataGateway.getStoryById(id);
            assertThat(storyById).isNotEmpty();
            return storyById.get().url().equals("www.example.com");
        };
    }
}