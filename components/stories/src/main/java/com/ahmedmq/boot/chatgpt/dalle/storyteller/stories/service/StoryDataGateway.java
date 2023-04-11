package com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

@Component
public class StoryDataGateway {

    private final DataSource dataSource;

    public StoryDataGateway(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveStory(String title, String description) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into story(title, detail, created_at)" +
                            "values( ?, ? , ?)"
            )) {

                statement.setString(1, title);
                statement.setString(2, description);
                statement.setTimestamp(3, Timestamp.from(Instant.now()));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
