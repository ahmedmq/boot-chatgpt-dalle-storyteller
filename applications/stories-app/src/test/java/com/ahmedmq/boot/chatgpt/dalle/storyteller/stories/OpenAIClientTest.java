package com.ahmedmq.boot.chatgpt.dalle.storyteller.stories;


import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.OpenAIClient;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.chat.ChatCompletionRequest;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.chat.ChatCompletionResponse;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.chat.ChatMessage;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.chat.ChatRole;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class OpenAIClientTest {

   @RegisterExtension
   static WireMockExtension wireMockServer = WireMockExtension
           .newInstance()
           .options(wireMockConfig().dynamicPort())
           .build();

   @DynamicPropertySource
   static void register(DynamicPropertyRegistry registry){
       registry.add("openai.urls.base-url", wireMockServer::baseUrl);
   }

    @Autowired
    OpenAIClient openAIClient;

    @Test
    void shouldGenerateChatResponse() {
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

        ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest("gpt-3.5-turbo",
                Collections.singletonList(new ChatMessage(ChatRole.user, "Hello World")));
        ChatCompletionResponse chatCompletionResponse = openAIClient.chat(chatCompletionRequest);

        assertThat(chatCompletionResponse.choices()).isNotEmpty();

        ChatMessage chatMessage = chatCompletionResponse.choices().get(0).message();

        assertThat(chatMessage.role()).isEqualTo(ChatRole.assistant);
        assertThat(chatMessage.content()).isEqualTo("\n\nHello there, how may I assist you today?");
    }
}
