package com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service;

import com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service.openai.OpenAIClient;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service.openai.OpenAIClientConfig;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service.openai.model.ChatCompletionRequest;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service.openai.model.ChatCompletionResponse;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service.openai.model.ChatMessage;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service.openai.model.ChatRole;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@EnableFeignClients(basePackageClasses = OpenAIClient.class)
@ComponentScan(basePackageClasses = OpenAIClientConfig.class)
public class StoryService {

    private final OpenAIClient openAIClient;
    private final OpenAIClientConfig openAIClientConfig;

    private final StoryDataGateway storyDataGateway;

    public StoryService(OpenAIClient openAIClient, OpenAIClientConfig openAIClientConfig, StoryDataGateway storyDataGateway) {
        this.openAIClient = openAIClient;
        this.openAIClientConfig = openAIClientConfig;
        this.storyDataGateway = storyDataGateway;
    }

    public void createStory() {
        String[] characters = new String[]{"Ethan", "Ryan"};
        String scene = "Pair programming together when suddenly powers shuts off";
        String prompt = """
                Write a title and a rhyming story on %d main characters called %s.
                  The story needs to be set within the scene %s and be at least 200 words long
                """.formatted(characters.length, String.join(" and ", characters),
                scene);
        ChatCompletionResponse chatCompletionResponse = this.openAIClient
                .chat(
                        new ChatCompletionRequest(openAIClientConfig.getModel(),
                                Collections.singletonList(new ChatMessage(ChatRole.user, prompt))
                        )
                );

        String story = Objects.requireNonNullElse(chatCompletionResponse.choices().get(0).message().content(),"");
        String[] parts = story.split("\n", 2);
        if ( parts.length == 2) {
            storyDataGateway.saveStory(parts[0], parts[1], characters, scene);
        }else {
            System.out.println("Unexpected output from OpenAI");
        }

    }
}
