package com.ahmedmq.boot.chatgpt.dalle.storyteller.images;

import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.image.ImageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test")
class CreateStoryImageFunctionTest {

    @DynamicPropertySource
    static void register(DynamicPropertyRegistry registry) {
        System.out.println("Setting up dynamic registry");
        registry.add("openai.urls.base-url", wireMockServer::baseUrl);
    }

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension
            .newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @Autowired
    InputDestination inputDestination;

    @Autowired
    OutputDestination outputDestination;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testImageFlow() throws JsonProcessingException {

        wireMockServer.stubFor(
                post("/images/generations")
                        .withHeader("Authorization", equalTo("Bearer abc"))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBodyFile("image-generation-response.json")
                        )
        );

        StoryEvent storyEvent = new StoryEvent(1, "Title", "Scene", Instant.now().toEpochMilli());
        inputDestination.send(MessageBuilder.withPayload(storyEvent).build(), "stories-database.stories.story");

        Message<byte[]> received = outputDestination.receive(1000, "stories.images");
        String payload = new String(received.getPayload(), StandardCharsets.UTF_8);
        ImageResult imageResult = objectMapper.readValue(payload, ImageResult.class);
        assertThat(received.getHeaders().get("X-STORY-ID")).isEqualTo(1L);
        assertThat(imageResult.data().get(0).url()).isEqualTo("https://www.example.com");

    }

}