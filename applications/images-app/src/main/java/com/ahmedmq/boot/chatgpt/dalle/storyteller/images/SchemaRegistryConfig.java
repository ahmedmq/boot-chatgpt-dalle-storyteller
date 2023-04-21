package com.ahmedmq.boot.chatgpt.dalle.storyteller.images;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.schema.registry.client.ConfluentSchemaRegistryClient;
import org.springframework.cloud.schema.registry.client.EnableSchemaRegistryClient;
import org.springframework.cloud.schema.registry.client.SchemaRegistryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableSchemaRegistryClient
public class SchemaRegistryConfig {

    @Primary
    @Bean
    SchemaRegistryClient confluentSchemaRegistryClient(@Value("${spring.cloud.schemaRegistryClient.endpoint}") String endpoint) {
        ConfluentSchemaRegistryClient client = new ConfluentSchemaRegistryClient();
        client.setEndpoint(endpoint);
        return client;
    }
}
