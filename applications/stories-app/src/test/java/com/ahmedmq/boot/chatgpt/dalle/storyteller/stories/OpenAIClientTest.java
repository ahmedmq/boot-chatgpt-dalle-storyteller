package com.ahmedmq.boot.chatgpt.dalle.storyteller.stories;


import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.OpenAIClient;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.chat.ChatCompletionRequest;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.chat.ChatCompletionResponse;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.chat.ChatMessage;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.chat.ChatRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"spring.flyway.enabled=false"})
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
    void shouldGenerateChatResponse() throws JsonProcessingException {
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

        // Verify if request made was accurate
        wireMockServer.verify(1, postRequestedFor(urlEqualTo("/chat/completions")));
        List<LoggedRequest> loggedRequests = wireMockServer.findAll(postRequestedFor(urlMatching("/chat/completions")));
        assertThat(loggedRequests.size()).isEqualTo(1);
        LoggedRequest loggedRequest = loggedRequests.get(0);
        assertThat(loggedRequest.getHeader("Authorization")).isEqualTo("Bearer abc");
        assertThat(loggedRequest.getHeader("Content-Type")).isEqualTo("application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        ChatCompletionRequest actualChatCompletionRequest = objectMapper.readValue(new String(loggedRequest.getBody()), ChatCompletionRequest.class);

        // Verify if response is accurate
        assertThat(actualChatCompletionRequest).isEqualTo(chatCompletionRequest).usingRecursiveComparison();
        assertThat(chatCompletionResponse.choices()).isNotEmpty();
        ChatMessage chatMessage = chatCompletionResponse.choices().get(0).message();
        assertThat(chatMessage.role()).isEqualTo(ChatRole.assistant);
        assertThat(chatMessage.content()).isEqualTo("Title: The Dark Programming Duo\n\nEthan and Lisa were the ultimate programming duo. They spent countless hours working together on various projects, pushing each other to new levels of creativity and innovation. Today, they were pair programming on their latest project, with Lisa typing away on her computer while Ethan offered input and feedback. They were so engrossed in their work that they barely noticed as the sun began to set and the room grew darker.\n\nSuddenly, the power shut off. The room was plunged into darkness, and Ethan and Lisa were left staring at their blank computer screens in confusion. They paused for a moment, unsure of what to do next.\n\nBut then Ethan's mind started racing. He knew that they couldn't afford to waste any time. They were on a tight deadline, and every moment counted. He quickly grabbed a flashlight from his bag and shone it over Lisa's computer screen, illuminating it enough for them to see.\n\nWithout missing a beat, they got back to work. Lisa started navigating the code on her screen, writing down notes on paper as she tried to keep track of where they were. Meanwhile, Ethan used the flashlight to search through their codebooks, looking for any critical information they might need.\n\nAs they worked, the darkness seemed to fade away, replaced by the light of their computer screens and the glow of their determination. Hours passed, and they kept at it, making steady progress despite the setback.\n\nFinally, as the first rays of sunlight began to seep through the windows, they finished. They leaned back in their chairs, exhausted but triumphant. They had pulled it off, even in the face of unexpected obstacles.\n\nAs they packed up their things, they both knew that they had become an even stronger team through this experience. They had proven to themselves and to each other that, no matter how tough the challenge, they could face it together and come out on top.");
    }
}
