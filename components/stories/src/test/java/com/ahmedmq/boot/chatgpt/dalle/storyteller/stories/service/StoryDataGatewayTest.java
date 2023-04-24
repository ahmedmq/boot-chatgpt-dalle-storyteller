package com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class StoryDataGatewayTest {

    public DataSource dataSource() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerName("localhost");
        ds.setDatabaseName("stories-database");
        ds.setUser("stories");
        ds.setPassword("password");
        ds.setPortNumbers(new int[]{5450});
        return ds;
    }

    @BeforeEach
    public void emptyDB() throws SQLException {
        try(Connection connection = dataSource().getConnection()){
            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE from story")){
                statement.executeUpdate();
            }
        }
    }
    StoryDataGateway cut = new StoryDataGateway(dataSource());

    @Test
    void testSave() {

        Long savedStoryId = cut.saveStory("title", "description", new String[]{"new"}, "scene");
        try(final Connection connection = dataSource().getConnection()){
            try(final PreparedStatement statement = connection.prepareStatement(
                    "select * from story"
            )){
                try(final ResultSet resultSet = statement.executeQuery()){
                    assertThat(resultSet.next()).isTrue();
                    assertThat(resultSet.getLong(1)).isEqualTo(savedStoryId);
                    assertThat(resultSet.getString(2)).isEqualTo("title");
                    assertThat(resultSet.getString(3)).isEqualTo("description");
                    assertThat((String[]) resultSet.getArray(4).getArray()).isEqualTo( new String[]{"new"});
                    assertThat(resultSet.getString(5)).isEqualTo("scene");
                    assertThat(resultSet.getTimestamp(6)).isNotNull();
                }

            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetStoryById(){
        Long savedStoryId = cut.saveStory("title", "description", new String[]{"one"}, "scene");
        Optional<Story> story = cut.getStoryById(savedStoryId);
        assertThat(story).isNotEmpty();
        assertThat(story.get().title()).isEqualTo("title");
        assertThat(story.get().description()).isEqualTo("description");
        assertThat(story.get().scene()).isEqualTo("scene");
    }

    @Test
    void testUpdateStoryUrl(){
        Long savedStoryId = cut.saveStory("title", "description", new String[]{"one"}, "scene");
        Optional<Story> story = cut.getStoryById(savedStoryId);
        assertThat(story).isNotEmpty();
        assertThat(story.get().url()).isNull();
        cut.updateStoryImageUrl(savedStoryId, "www.test.com");
        Optional<Story> updatedStory = cut.getStoryById(savedStoryId);
        assertThat(updatedStory).isNotEmpty();
        assertThat(updatedStory.get().url()).isEqualTo("www.test.com");

    }
}