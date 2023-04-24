package com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.chat;

import java.util.List;

public record ChatCompletionRequest(String model, List<ChatMessage> messages) {
}
