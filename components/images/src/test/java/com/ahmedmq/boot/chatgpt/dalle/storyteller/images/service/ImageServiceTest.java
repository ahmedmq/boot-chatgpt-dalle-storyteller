package com.ahmedmq.boot.chatgpt.dalle.storyteller.images.service;

import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.OpenAIClient;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.image.CreateImageRequest;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.image.Image;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.image.ImageResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    OpenAIClient openAIClient;

    @InjectMocks
    ImageService cut;

    @Test
    void shouldInvokeOpenAIClient() {

        when(openAIClient.createImage(any(CreateImageRequest.class)))
                .thenReturn(
                        new ImageResult(
                                Instant.now().getEpochSecond(),
                                Collections.singletonList(
                                        new Image("url")
                                )
                        )
                );
        cut.createImage("test");
        ArgumentCaptor<CreateImageRequest> captor = ArgumentCaptor.forClass(CreateImageRequest.class);
        verify(openAIClient, times(1)).createImage(captor.capture());

        CreateImageRequest createImageRequest = captor.getValue();

        assertThat(createImageRequest.prompt()).isEqualTo("test");

    }
}