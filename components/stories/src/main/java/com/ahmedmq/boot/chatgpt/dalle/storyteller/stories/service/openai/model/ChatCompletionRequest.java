package com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service.openai.model;

import java.util.List;

public record ChatCompletionRequest(String model, List<ChatMessage> messages) {
}
