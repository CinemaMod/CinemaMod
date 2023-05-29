package com.cinemamod.bukkit.storage.sql.video;

import com.cinemamod.bukkit.service.VideoServiceType;
import com.cinemamod.bukkit.storage.sql.MySQLDriver;
import com.cinemamod.bukkit.storage.sql.SQLDriver;
import com.cinemamod.bukkit.storage.sql.SQLiteDriver;
import com.cinemamod.bukkit.video.VideoInfo;
import com.cinemamod.bukkit.video.VideoRequest;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SQLVideoStorage extends AbstractSQLVideoStorage {

    public SQLVideoStorage(SQLDriver driver) throws SQLException {
        super(driver);
    }

    @Override
    public void createTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            if (getDriver() instanceof SQLiteDriver) {
                statement.execute("CREATE TABLE IF NOT EXISTS video_info (" +
                        "id INTEGER, " +
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
            } else if (getDriver() instanceof MySQLDriver) {
                statement.execute("CREATE TABLE IF NOT EXISTS video_info (" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "service_type VARCHAR(16) NOT NULL, " +
                        "service_id VARCHAR(255) NOT NULL, " +
                        "title TEXT NOT NULL, " +
                        "poster TEXT NOT NULL, " +
                        "thumbnail_url TEXT NOT NULL, " +
                        "duration_seconds BIGINT NOT NULL, " +
                        "PRIMARY KEY (id), " +
                        "UNIQUE (service_type, service_id));");
                statement.execute("CREATE TABLE IF NOT EXISTS video_requests (" +
                        "requester VARCHAR(36) NOT NULL, " +
                        "video_info_id INT NOT NULL, " +
                        "last_requested BIGINT NOT NULL, " +
                        "times_requested INT NOT NULL DEFAULT 1, " +
                        "hidden BOOL NOT NULL DEFAULT 0, " +
                        "FOREIGN KEY (video_info_id) REFERENCES video_info(id), " +
                        "UNIQUE (requester, video_info_id));");
            }
        }
    }

    private RelationalVideoInfo buildVideoInfoFromResultSet(ResultSet resultSet) throws SQLException {
        int relId = resultSet.getInt("id");
        VideoServiceType service = VideoServiceType.valueOf(resultSet.getString("service_type"));
        String id = resultSet.getString("service_id");
        String title = resultSet.getString("title");
        String poster = resultSet.getString("poster");
        String thumbnailUrl = resultSet.getString("thumbnail_url");
        long durationSeconds = resultSet.getLong("duration_seconds");
        VideoInfo videoInfo = new VideoInfo(service, id, title, poster, thumbnailUrl, durationSeconds);
        return new RelationalVideoInfo(videoInfo, relId);
    }

    @Override
    protected RelationalVideoInfo queryVideoInfo(VideoServiceType videoService, String id, Connection connection) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM video_info WHERE service_type = ? AND service_id = ?;")) {
            query.setString(1, videoService.name());
            query.setString(2, id);

            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    return buildVideoInfoFromResultSet(resultSet);
                }
            }
        }

        return null;
    }

    @Override
    protected int insertVideoInfo(VideoInfo videoInfo, Connection connection) throws SQLException {
        try (PreparedStatement insert = connection.prepareStatement(
                "INSERT INTO video_info (service_type, service_id, title, poster, thumbnail_url, duration_seconds) VALUES (?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, videoInfo.getServiceType().name());
            insert.setString(2, videoInfo.getId());
            insert.setString(3, videoInfo.getTitle());
            insert.setString(4, videoInfo.getPoster());
            insert.setString(5, videoInfo.getThumbnailUrl());
            insert.setLong(6, videoInfo.getDurationSeconds());
            insert.execute();

            try (ResultSet resultSet = insert.getGeneratedKeys()) {
                if (resultSet.next()) return resultSet.getInt(1);
            }
        }

        return 0;
    }

    @Override
    protected void updateVideoInfo(RelationalVideoInfo relationalVideoInfo, Connection connection) throws SQLException {
        try (PreparedStatement update = connection.prepareStatement(
                "UPDATE video_info SET title = ?, poster = ?, thumbnail_url = ?, duration_seconds = ? WHERE id = ?;")) {
            update.setString(1, relationalVideoInfo.getTitle());
            update.setString(2, relationalVideoInfo.getPoster());
            update.setString(3, relationalVideoInfo.getThumbnailUrl());
            update.setLong(4, relationalVideoInfo.getDurationSeconds());
            update.setInt(5, relationalVideoInfo.getRelId());
            update.execute();
        }
    }

    @Override
    protected RelationalVideoRequest queryVideoRequest(UUID requester, RelationalVideoInfo videoInfo, Connection connection) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM video_requests WHERE requester = ? and video_info_id = ?;")) {
            query.setString(1, requester.toString());
            query.setInt(2, videoInfo.getRelId());

            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    long lastRequested = resultSet.getLong("last_requested");
                    int timesRequested = resultSet.getInt("times_requested");
                    boolean hidden = resultSet.getBoolean("hidden");
                    return new RelationalVideoRequest(requester, videoInfo, lastRequested, timesRequested, hidden);
                }
            }
        }

        return null;
    }

    @Override
    protected void insertVideoRequest(VideoRequest videoRequest, RelationalVideoInfo videoInfo, Connection connection) throws SQLException {
        try (PreparedStatement insert = connection.prepareStatement(
                "INSERT INTO video_requests (requester, video_info_id, last_requested) VALUES (?, ?, ?);")) {
            insert.setString(1, videoRequest.getRequester().toString());
            insert.setInt(2, videoInfo.getRelId());
            insert.setLong(3, videoRequest.getLastRequested());
            insert.execute();
        }
    }

    @Override
    protected void updateVideoRequest(RelationalVideoRequest videoRequest, Connection connection) throws SQLException {
        try (PreparedStatement update = connection.prepareStatement(
                "UPDATE video_requests SET last_requested = ?, times_requested = ?, hidden = ? WHERE requester = ? AND video_info_id = ?;")) {
            update.setLong(1, videoRequest.getLastRequested());
            update.setInt(2, videoRequest.getTimesRequested());
            update.setBoolean(3, videoRequest.isHidden());
            update.setString(4, videoRequest.getRequester().toString());
            update.setInt(5, videoRequest.getRelationalVideoInfo().getRelId());
            update.execute();
        }
    }

    @Override
    protected Set<VideoRequest> queryVideoRequests(UUID requester, Connection connection) throws SQLException {
        Set<VideoRequest> videoRequests = new HashSet<>();

        try (PreparedStatement query = connection.prepareStatement(
                "SELECT * FROM video_requests INNER JOIN video_info ON video_requests.video_info_id = video_info.id WHERE requester = ?;")) {
            query.setString(1, requester.toString());

            try (ResultSet resultSet = query.executeQuery()) {
                while (resultSet.next()) {
                    VideoInfo videoInfo = buildVideoInfoFromResultSet(resultSet);
                    long lastRequested = resultSet.getLong("last_requested");
                    int timesRequested = resultSet.getInt("times_requested");
                    boolean hidden = resultSet.getBoolean("hidden");
                    VideoRequest videoRequest = new VideoRequest(requester, videoInfo, lastRequested, timesRequested, hidden);
                    videoRequests.add(videoRequest);
                }
            }
        }

        return videoRequests;
    }

}
