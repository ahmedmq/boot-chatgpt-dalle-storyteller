package com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.chat;

import java.util.List;

public record ChatCompletionResponse(String id, String object, String created, List<ChatCompletionChoice> choices) {
}
