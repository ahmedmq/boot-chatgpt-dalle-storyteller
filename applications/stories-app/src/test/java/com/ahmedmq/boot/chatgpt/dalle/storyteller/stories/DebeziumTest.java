package com.ahmedmq.boot.chatgpt.dalle.storyteller.stories;

import com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service.StoryService;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.jayway.jsonpath.JsonPath;
import io.debezium.testing.testcontainers.ConnectorConfiguration;
import io.debezium.testing.testcontainers.DebeziumContainer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {""})
@ActiveProfiles("test")
public class DebeziumTest {

    private static final Network network = Network.newNetwork();

    private static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.1"))
            .withNetwork(network);

    public static DebeziumContainer debeziumContainer =
            new DebeziumContainer("quay.io/debezium/connect:2.2.0.Final")
                    .withNetwork(network)
                    .withKafka(kafkaContainer)
                    .dependsOn(kafkaContainer);

    public static PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>(
                    DockerImageName.parse("postgres:14-bullseye"))
                    .withCommand("postgres", "-c", "wal_level=logical")
                    .withDatabaseName("stories-database")
                    .withUsername("stories")
                    .withPassword("password")
                    .withNetwork(network);

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension
            .newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    static {
        Startables.deepStart(Stream.of(
                        kafkaContainer, postgresContainer, debeziumContainer))
                .join();
    }

    @DynamicPropertySource
    static void register(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.stream.kafka.binder.brokers", kafkaContainer::getBootstrapServers);
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("openai.urls.base-url", wireMockServer::baseUrl);
    }

    @Autowired
    StoryService storyService;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public Consumer<String> read() {
            return event -> {
                System.out.println("Received message in Test");
                System.out.println(event);
            };
        }
    }

    @Test
    void testDebezium() throws InterruptedException {

        wireMockServer.stubFor(
                post("/chat/completions")
                        .withHeader("Authorization", equalTo("Bearer abc"))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBodyFile("chat-completion-response.json")
                        )
        );

        ConnectorConfiguration connectorConfiguration = ConnectorConfiguration
                .forJdbcContainer(postgresContainer)
                .with("connector.class", "io.debezium.connector.postgresql.PostgresConnector")
                .with("plugin.name", "pgoutput")
                .with("publication.autocreate.mode", "filtered")
                .with("topic.prefix", "stories-database")
                .with("transforms", "unwrap,AddNamespace")
                .with("transforms.unwrap.type", "io.debezium.transforms.ExtractNewRecordState")
                .with("transforms.AddNamespace.type", "org.apache.kafka.connect.transforms.SetSchemaMetadata$Value")
                .with("transforms.AddNamespace.schema.name", "com.ahmedmq.boot.chatgpt.dalle.storyteller.images.StoryEvent");

        debeziumContainer.registerConnector("stories-connector", connectorConfiguration);

        Long storyId = storyService.createStory();

        try (KafkaConsumer<String, String> consumer = getConsumer()) {

            consumer.subscribe(List.of("stories-database.stories.story"));

            List<ConsumerRecord<String, String>> changeEvents =
                    drain(consumer);

            assertThat(JsonPath.<Integer>read(changeEvents.get(0).value(),
                    "$.id")).isEqualTo(storyId.intValue());

            assertThat(JsonPath.<String>read(changeEvents.get(0).value(),
                    "$.title")).isEqualTo(" The Dark Programming Duo");

            assertThat(JsonPath.<String>read(changeEvents.get(0).value(),
                    "$.scene")).isEqualTo("Pair programming together when suddenly powers shuts off");

            consumer.unsubscribe();
        }

    }

    private KafkaConsumer<String, String> getConsumer() {

        return new KafkaConsumer<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                        DebeziumTest.kafkaContainer.getBootstrapServers(),
                        ConsumerConfig.GROUP_ID_CONFIG,
                        "tc-" + UUID.randomUUID(),
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                        "earliest"),
                new StringDeserializer(),
                new StringDeserializer());
    }

    private List<ConsumerRecord<String, String>> drain(
            KafkaConsumer<String, String> consumer) {

        List<ConsumerRecord<String, String>> allRecords = new ArrayList<>();

        Unreliables.retryUntilTrue(10, TimeUnit.SECONDS, () -> {
            consumer.poll(Duration.ofMillis(50))
                    .iterator()
                    .forEachRemaining(allRecords::add);

            return allRecords.size() == 1;
        });

        return allRecords;
    }
}


