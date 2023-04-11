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

@Service
@EnableFeignClients(basePackageClasses = OpenAIClient.class)
@ComponentScan(basePackageClasses = OpenAIClientConfig.class)
public class CreateStoryService {

    private final OpenAIClient openAIClient;
    private final OpenAIClientConfig openAIClientConfig;

    public CreateStoryService(OpenAIClient openAIClient, OpenAIClientConfig openAIClientConfig) {
        this.openAIClient = openAIClient;
        this.openAIClientConfig = openAIClientConfig;
    }

    public void createStory() {
        List<String> characters = Arrays.asList("Ethan", "Ryan");
        String prompt = """
                Write a title and a rhyming story on %d main characters called %s.
                  The story needs to be set within the scene %s and be at least 200 words long
                """.formatted(characters.size(), String.join(" and ", characters),
                "Pair programming together when suddenly powers shuts off");
        ChatCompletionResponse chatCompletionResponse = this.openAIClient
                .chat(
                        new ChatCompletionRequest(openAIClientConfig.getModel(),
                                Collections.singletonList(new ChatMessage(ChatRole.user, prompt))
                        )
                );
        System.out.println(chatCompletionResponse.choices().get(0).message().content());
    }
}
