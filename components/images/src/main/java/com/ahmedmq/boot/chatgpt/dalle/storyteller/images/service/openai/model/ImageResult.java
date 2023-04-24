package com.ahmedmq.boot.chatgpt.dalle.storyteller.images.service.openai.model;

import java.util.List;

public record ImageResult(Long created, List<Image> data) {
}
