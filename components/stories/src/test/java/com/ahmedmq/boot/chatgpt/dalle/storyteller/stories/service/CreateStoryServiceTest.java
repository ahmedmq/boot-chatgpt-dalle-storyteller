package com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service;


import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.OpenAIClient;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.OpenAIClientConfig;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.chat.ChatCompletionChoice;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.chat.ChatCompletionRequest;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.chat.ChatCompletionResponse;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.chat.ChatMessage;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.chat.ChatRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateStoryServiceTest {

    @Mock
    OpenAIClient openAIClient;

    @Mock
    OpenAIClientConfig openAIClientConfig;

    @InjectMocks
    StoryService sut;

    @Test
    void shouldReturnMockOpenAIResponse() {

        ChatCompletionChoice chatCompletionChoice =
                new ChatCompletionChoice(
                        new ChatMessage(ChatRole.assistant,
                                "")
                );

        ChatCompletionResponse chatCompletionResponse =
                new ChatCompletionResponse(
                        "chatcmpl-123",
                        "chat.completion",
                        "1677652288",
                        Collections.singletonList(chatCompletionChoice)
                );

        when(openAIClient.chat(any(ChatCompletionRequest.class)))
                .thenReturn(chatCompletionResponse);

        when(openAIClientConfig.getModel()).thenReturn("gpt-3.5-turbo");

        sut.createStory();

        ArgumentCaptor<ChatCompletionRequest> captor = ArgumentCaptor.forClass(ChatCompletionRequest.class);

        verify(openAIClient, times(1)).chat(captor.capture());

        ChatCompletionRequest request = captor.getValue();

        assertThat(request.model()).isEqualTo("gpt-3.5-turbo");
        assertThat(request.messages()).isNotEmpty();
        assertThat(request.messages().get(0).role()).isEqualTo(ChatRole.user);

        verify(openAIClientConfig, times(1)).getModel();


    }
}