package com.cinemamod.bukkit.storage.sql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteVideoStorage extends GenericSQLVideoStorage {

    private final File dbFile;

    public SQLiteVideoStorage(File dbFile) throws IOException, SQLException {
        this.dbFile = dbFile;

        if (!dbFile.exists()) {
            dbFile.createNewFile();
        }

        try (Connection connection = createConnection()) {
            createTables(connection);
        }
    }

    @Override
    protected void createTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS video_info (" +
                    "id INT, " +
                    "service_type VARCHAR(16), " +
                    "service_id VARCHAR(255), " +
                    "title TEXT, " +
                    "poster TEXT, " +
                    "thumbnail_url TEXT, " +
                    "duration_seconds BIGINT, " +
                    "PRIMARY KEY (id), " +
                    "UNIQUE (service_type, service_id));");
            statement.execute("CREATE TABLE IF NOT EXISTS video_requests (" +
                    "requester VARCHAR(36), " +
                    "video_info_id INT, " +
                    "last_requested BIGINT, " +
                    "times_requested INT DEFAULT 1, " +
                    "hidden BOOL DEFAULT 0, " +
                    "FOREIGN KEY (video_info_id) REFERENCES video_info(id), " +
                    "UNIQUE (requester, video_info_id));");
        }
    }

    @Override
    protected Connection createConnection() throws SQLException {
        String jdbcUrl = "jdbc:sqlite:" + dbFile.getName();
        return DriverManager.getConnection(jdbcUrl);
    }

}
