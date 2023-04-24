package com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service;

import java.time.Instant;
import java.util.List;

public record Story(Long id, String title, String description, List<String> characters, String scene, String url, Instant createdAt) {
}
