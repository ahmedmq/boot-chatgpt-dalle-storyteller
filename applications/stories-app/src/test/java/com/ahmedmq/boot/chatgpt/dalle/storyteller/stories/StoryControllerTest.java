package com.ahmedmq.boot.chatgpt.dalle.storyteller.stories;

import com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service.Story;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service.StoryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class StoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    StoryService storyService;

    @Test
    void testGetStory() throws Exception {

        Story newStory = new Story(1L,
                "title",
                "description",
                Collections.singletonList("Ethan"),
                "scene",
                "www.example.com",
                Instant.now());

        Mockito.when(storyService.getStory())
                .thenReturn(
                        newStory);

        mockMvc.perform(get("/getStory"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.title", is(newStory.title())),
                        jsonPath("$.description", is(newStory.description())),
                        jsonPath("$.url", is(newStory.url()))
                );


    }

    @Test
    void testGenerate() throws Exception {
        mockMvc.perform(post("/generate"))
                .andExpect(status().isOk());

        verify(storyService, times(1)).createStory();
    }
}