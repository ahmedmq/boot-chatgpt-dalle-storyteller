package com.ahmedmq.boot.chatgpt.dalle.storyteller.openai.model.image;

import java.util.List;

public record ImageResult(Long created, List<Image> data) {
}
