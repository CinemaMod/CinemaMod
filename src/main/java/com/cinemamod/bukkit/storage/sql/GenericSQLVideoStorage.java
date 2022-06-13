package com.cinemamod.bukkit.storage.sql;

import com.cinemamod.bukkit.service.VideoServiceType;
import com.cinemamod.bukkit.storage.VideoInfo;
import com.cinemamod.bukkit.storage.VideoPlaylist;
import com.cinemamod.bukkit.storage.VideoRequest;

import javax.annotation.Nullable;
import java.sql.*;
import java.util.*;

public abstract class GenericSQLVideoStorage extends SQLVideoStorage {

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

    @Nullable
    @Override
    protected RelationalVideoPlaylist queryPlaylist(UUID owner, String name, Connection connection) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("SELECT id FROM video_playlists WHERE owner = ? AND name = ?;")) {
            query.setString(1, owner.toString());
            query.setString(2, name);

            try (ResultSet resultSet = query.executeQuery()) {
                if (resultSet.next()) {
                    int relId = resultSet.getInt("id");
                    VideoPlaylist videoPlaylist = new VideoPlaylist(owner, name);
                    return new RelationalVideoPlaylist(videoPlaylist, relId);
                }
            }
        }

        return null;
    }

    @Override
    protected int insertPlaylist(VideoPlaylist playlist, Connection connection) throws SQLException {
        try (PreparedStatement insert = connection.prepareStatement("INSERT INTO video_playlists (owner, name) VALUES (?, ?);", Statement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, playlist.getOwner().toString());
            insert.setString(2, playlist.getName());
            insert.execute();

            try (ResultSet resultSet = insert.getGeneratedKeys()) {
                if (resultSet.next()) return resultSet.getInt(1);
            }
        }

        return 0;
    }

    @Override
    protected void updatePlaylist(RelationalVideoPlaylist playlist, Connection connection) throws SQLException {
        try (PreparedStatement update = connection.prepareStatement("UPDATE video_playlists SET name = ? WHERE id = ?;")) {
            update.setString(1, playlist.getName());
            update.setInt(2, playlist.getRelId());
            update.execute();
        }
    }

    @Override
    protected void deletePlaylist(RelationalVideoPlaylist playlist, Connection connection) throws SQLException {
        try (PreparedStatement delete = connection.prepareStatement("DELETE FROM video_playlist_videos WHERE video_playlist_id = ?;")) {
            delete.setInt(1, playlist.getRelId());
            delete.execute();
        }

        try (PreparedStatement delete = connection.prepareStatement("DELETE FROM video_playlists WHERE id = ?;")) {
            delete.setInt(1, playlist.getRelId());
            delete.execute();
        }
    }

    @Override
    protected void insertPlaylistVideo(RelationalVideoPlaylist playlist, RelationalVideoInfo videoInfo, Connection connection) throws SQLException {
        try (PreparedStatement insert = connection.prepareStatement(
                "INSERT INTO video_playlist_videos (video_playlist_id, video_info_id) VALUES (?, ?);")) {
            insert.setInt(1, playlist.getRelId());
            insert.setInt(2, videoInfo.getRelId());
            insert.execute();
        }
    }

    @Override
    protected void deletePlaylistVideo(RelationalVideoPlaylist playlist, RelationalVideoInfo videoInfo, Connection connection) throws SQLException {
        try (PreparedStatement delete = connection.prepareStatement("DELETE FROM video_playlist_videos WHERE video_playlist_id = ? AND video_info_id = ?;")) {
            delete.setInt(1, playlist.getRelId());
            delete.setInt(2, videoInfo.getRelId());
            delete.execute();
        }
    }

    @Override
    protected List<VideoInfo> queryPlaylistVideos(int playlistId, Connection connection) throws SQLException {
        List<VideoInfo> videos = new ArrayList<>();

        try (PreparedStatement query = connection.prepareStatement(
                "SELECT * FROM video_playlist_videos INNER JOIN video_info ON video_playlist_videos.video_info_id = video_info.id WHERE video_playlist_id = ?;")) {
            query.setInt(1, playlistId);

            try (ResultSet resultSet = query.executeQuery()) {
                while (resultSet.next()) {
                    videos.add(buildVideoInfoFromResultSet(resultSet));
                }
            }
        }

        return videos;
    }

    @Override
    protected List<VideoPlaylist> queryPlaylists(UUID owner, Connection connection) throws SQLException {
        List<VideoPlaylist> playlists = new ArrayList<>();

        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM video_playlists WHERE owner = ?;")) {
            query.setString(1, owner.toString());

            try (ResultSet resultSet = query.executeQuery()) {
                while (resultSet.next()) {
                    int relId = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    List<VideoInfo> videos = queryPlaylistVideos(relId, connection);
                    VideoPlaylist playlist = new VideoPlaylist(owner, name, new HashSet<>(videos));
                    playlists.add(new RelationalVideoPlaylist(playlist, relId));
                }
            }
        }

        return playlists;
    }

    @Override
    protected void insertVideoPlaylistVideo(RelationalVideoPlaylist playlist, RelationalVideoInfo video, Connection connection) throws SQLException {
        try (PreparedStatement insert = connection.prepareStatement("INSERT INTO video_playlist_videos (video_playlist_id, video_info_id) VALUES (?, ?);")) {
            insert.setInt(1, playlist.getRelId());
            insert.setInt(2, video.getRelId());
            insert.execute();
        }
    }

    @Override
    protected void deleteVideoFromPlaylist(RelationalVideoPlaylist playlist, RelationalVideoInfo video, Connection connection) throws SQLException {
        try (PreparedStatement delete = connection.prepareStatement("DELETE FROM video_playlist_videos WHERE video_playlist_id = ? AND video_info_id = ?;")) {
            delete.setInt(1, playlist.getRelId());
            delete.setInt(2, video.getRelId());
            delete.execute();
        }
    }

    @Override
    protected void createTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
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
            statement.execute("CREATE TABLE IF NOT EXISTS video_playlists (" +
                    "id INT NOT NULL AUTO_INCREMENT, " +
                    "owner VARCHAR(36) NOT NULL, " +
                    "name VARCHAR(32) NOT NULL, " +
                    "PRIMARY KEY (id), " +
                    "UNIQUE (owner, name));");
            statement.execute("CREATE TABLE IF NOT EXISTS video_playlist_videos (" +
                    "video_playlist_id INT NOT NULL, " +
                    "video_info_id INT NOT NULL, " +
                    "FOREIGN KEY (video_playlist_id) REFERENCES video_playlists(id), " +
                    "FOREIGN KEY (video_info_id) REFERENCES video_info(id), " +
                    "UNIQUE (video_playlist_id, video_info_id));");
        }
    }

}
