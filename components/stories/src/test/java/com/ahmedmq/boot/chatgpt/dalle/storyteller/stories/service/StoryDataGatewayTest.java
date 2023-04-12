package com.ahmedmq.boot.chatgpt.dalle.storyteller.stories.service;

import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class StoryDataGatewayTest {

    public DataSource dataSource() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerName("localhost");
        ds.setDatabaseName("test");
        ds.setUser("postgres");
        ds.setPassword("postgres");
        return ds;
    }

    StoryDataGateway cut = new StoryDataGateway(dataSource());

    @Test
    void testSave() {

        cut.saveStory("title", "description", new String[]{"one"}, "scene" );
        try(final Connection connection = dataSource().getConnection()){
            try(final PreparedStatement statement = connection.prepareStatement(
                    "select * from story"
            )){
                try(final ResultSet resultSet = statement.executeQuery()){
                    assertThat(resultSet.next()).isTrue();
                    assertThat(resultSet.getLong(1)).isNotNull();
                    assertThat(resultSet.getString(2)).isEqualTo("title");
                    assertThat(resultSet.getString(3)).isEqualTo("description");
                    assertThat((String[]) resultSet.getArray(4).getArray()).isEqualTo( new String[]{"one"});
                    assertThat(resultSet.getString(5)).isEqualTo("scene");
                    assertThat(resultSet.getTimestamp(6)).isNotNull();
                }

            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}