package com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Component
public class StoryDataGateway {

    private final DataSource dataSource;

    public StoryDataGateway(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveStory(String title, String description, String[] characters, String scene) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into story(title, detail, characters, scene, created_at)" +
                            "values( ?, ? , ?, ?, ?)"
            )) {

                Array characterArray = connection.createArrayOf("text", characters);
                statement.setString(1, title);
                statement.setString(2, description);
                statement.setArray(3, characterArray);
                statement.setString(4, scene);
                statement.setTimestamp(5, Timestamp.from(Instant.now()));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
