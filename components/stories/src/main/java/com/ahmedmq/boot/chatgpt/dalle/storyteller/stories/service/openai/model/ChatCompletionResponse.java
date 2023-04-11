package com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service.openai.model;

import java.util.List;

public record ChatCompletionResponse(String id, String object, String created, List<ChatCompletionChoice> choices) {
}
