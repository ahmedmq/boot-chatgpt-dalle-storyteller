package com.ahmedmq.boot.chatgpt.dalle.storyteller.stories;

import com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service.Story;
import com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service.StoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StoryController {

    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }


    @GetMapping("/getStory")
    public StoryData getStory(){
        Story story = storyService.getStory();
        return new StoryData(story.title(), story.description(), story.url());
    }

    @PostMapping("/generate")
    public void generate(){
        storyService.createStory();
    }

    record StoryData(String title, String description, String url){}

}
