package com.ahmedmq.boot.chatgpt.dalle.storyteller.stories;

import com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service.CreateStoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StoriesAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoriesAppApplication.class, args);
    }

    @Bean
    @Profile("!test")
    CommandLineRunner commandLineRunner(CreateStoryService createStoryService){
        return args -> createStoryService.createStory();
    }

}
