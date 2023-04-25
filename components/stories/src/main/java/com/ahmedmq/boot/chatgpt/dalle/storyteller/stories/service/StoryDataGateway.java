package com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

@Component
public class StoryDataGateway {

    private final DataSource dataSource;

    public StoryDataGateway(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Long saveStory(String title, String description, String[] characters, String scene) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into story(title, description, characters, scene, created_at)" +
                            "values( ?, ? , ?, ?, ?)", Statement.RETURN_GENERATED_KEYS
            )) {

                Array characterArray = connection.createArrayOf("text", characters);
                statement.setString(1, title);
                statement.setString(2, description);
                statement.setArray(3, characterArray);
                statement.setString(4, scene);
                statement.setTimestamp(5, Timestamp.from(Instant.now()));
                statement.executeUpdate();
                try(ResultSet rs = statement.getGeneratedKeys()){
                    rs.next();
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Story> getStoryById(Long id){
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(
                    "select id, title, description, characters, scene, url, created_at " +
                            "from story where id = ?"
            )){
                statement.setLong(1, id);
                try(ResultSet rs = statement.executeQuery()){
                    if(!rs.next()){
                        return Optional.empty();
                    }
                    Long storyId = rs.getLong(1);
                    String title = rs.getString(2);
                    String description = rs.getString(3);
                    String[] characterArray = (String[]) rs.getArray(4).getArray();
                    String scene = rs.getString(5);
                    String url = rs.getString(6);
                    Timestamp createdAt = rs.getTimestamp(7);

                    return Optional.of( new Story(storyId,title,description, Arrays.stream(characterArray).toList(),scene,url,createdAt.toInstant()));
                }

            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void updateStoryImageUrl(Long id, String url){
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "update story set url = ? where id = ?"
            )) {
                statement.setString(1, url);
                statement.setLong(2, id);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Story getLatestStory() {
        try(Connection connection = dataSource.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(
                    "select id, title, description, characters, scene, url, created_at " +
                            "from story " +
                            "where url <> ''" +
                            "order by created_at desc limit 1"
            )){
                try(ResultSet rs = statement.executeQuery()){
                    if(rs.next()) {
                        String[] characterArray = (String[]) rs.getArray(4).getArray();
                        Timestamp createdAt = rs.getTimestamp(7);
                        return new Story(rs.getLong(1),
                                rs.getString(2),
                                rs.getString(3),
                                Arrays.stream(characterArray).toList(),
                                rs.getString(5),
                                rs.getString(6),
                                createdAt.toInstant());
                    }else {
                        throw new RuntimeException("No rows found in table");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
