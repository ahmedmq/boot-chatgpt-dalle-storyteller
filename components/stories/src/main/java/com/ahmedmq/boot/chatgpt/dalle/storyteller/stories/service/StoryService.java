package com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service;


import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.OpenAIClient;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.OpenAIClientConfig;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.chat.ChatCompletionRequest;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.chat.ChatCompletionResponse;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.chat.ChatMessage;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.chat.ChatRole;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.image.ImageResult;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
        String[] characters = new String[]{"Ethan", "Lisa"};
        String scene = "Pair programming together when suddenly powers shuts off";
        String prompt = """
                Write a title and a story on %d main characters called %s.
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
            storyDataGateway.saveStory(parts[0].substring(6), parts[1], characters, scene);
        }else {
            System.out.println("Unexpected output from OpenAI");
        }

    }

    public void updateStoryWithImage(Long storyId, ImageResult imageResult){
        storyDataGateway.updateStoryImageUrl(storyId, imageResult.data().get(0).url());
    }

    public Story getStory() {
        return storyDataGateway.getLatestStory();
    }
}
