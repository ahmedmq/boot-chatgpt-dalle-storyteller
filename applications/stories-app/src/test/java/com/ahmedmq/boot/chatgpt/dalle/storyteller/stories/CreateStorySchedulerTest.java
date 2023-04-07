package com.ahmedmq.boot.chatgpt.dalle.storyteller.stories;

import com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service.CreateStoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CreateStorySchedulerTest {

    @Mock
    CreateStoryService createStoryService;

    @InjectMocks
    CreateStoryScheduler createStoryScheduler;

    @Test
    void shouldInvokeCreateStoryService() {
        createStoryScheduler.runTask();
        verify(createStoryService, times(1)).createStory();
    }
}
