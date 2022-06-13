package com.cinemamod.bukkit.storage.sql;

import com.cinemamod.bukkit.service.VideoServiceType;
import com.cinemamod.bukkit.storage.VideoInfo;
import com.cinemamod.bukkit.storage.VideoPlaylist;
import com.cinemamod.bukkit.storage.VideoRequest;
import com.cinemamod.bukkit.storage.VideoStorage;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class SQLVideoStorage extends VideoStorage {

    @Override
    public CompletableFuture<Void> saveVideoInfo(VideoInfo videoInfo) {
        return searchVideoInfo(videoInfo.getServiceType(), videoInfo.getId()).thenAcceptAsync(search -> {
            try (Connection connection = createConnection()) {
                if (search instanceof RelationalVideoInfo) {
                    updateVideoInfo(new RelationalVideoInfo(videoInfo, ((RelationalVideoInfo) search).getRelId()), connection);
                } else {
                    insertVideoInfo(videoInfo, connection);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<VideoInfo> searchVideoInfo(VideoServiceType videoService, String id) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = createConnection()) {
                return queryVideoInfo(videoService, id, connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        });
    }

    @Override
    public CompletableFuture<Void> saveVideoRequest(VideoRequest request) {
        return searchVideoInfo(request.getVideoInfo().getServiceType(), request.getVideoInfo().getId()).thenAcceptAsync(videoInfoSearch -> {
            if (videoInfoSearch instanceof RelationalVideoInfo) {
                try (Connection connection = createConnection()) {
                    RelationalVideoRequest videoRequestSearch = queryVideoRequest(request.getRequester(), (RelationalVideoInfo) videoInfoSearch, connection);

                    if (videoRequestSearch == null) {
                        insertVideoRequest(request, (RelationalVideoInfo) videoInfoSearch, connection);
                    } else {
                        updateVideoRequest(new RelationalVideoRequest(request, videoRequestSearch.getRelationalVideoInfo()), connection);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public CompletableFuture<Set<VideoRequest>> loadVideoRequests(UUID requester) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = createConnection()) {
                return queryVideoRequests(requester, connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        });
    }

    @Override
    public CompletableFuture<VideoPlaylist> searchPlaylist(UUID owner, String name) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = createConnection()) {
                return queryPlaylist(owner, name, connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        });
    }

    @Override
    public CompletableFuture<Void> savePlaylist(VideoPlaylist playlist) {
        return searchPlaylist(playlist.getOwner(), playlist.getName()).thenAccept(search -> {
            try (Connection connection = createConnection()) {
                if (search instanceof RelationalVideoPlaylist) {
                    updatePlaylist((RelationalVideoPlaylist) search, connection);
                } else {
                    insertPlaylist(playlist, connection);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> deletePlaylist(VideoPlaylist playlist) {
        return searchPlaylist(playlist.getOwner(), playlist.getName()).thenAcceptAsync(search -> {
            if (search instanceof RelationalVideoPlaylist) {
                try (Connection connection = createConnection()) {
                    deletePlaylist((RelationalVideoPlaylist) search, connection);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public CompletableFuture<List<VideoPlaylist>> loadPlaylists(UUID owner) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = createConnection()) {
                return queryPlaylists(owner, connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        });
    }

    @Override
    public CompletableFuture<Void> addVideoToPlaylist(VideoPlaylist playlist, VideoInfo video) {
        return searchPlaylist(playlist.getOwner(), playlist.getName())
                .thenAcceptAsync(playlistSearch -> searchVideoInfo(video.getServiceType(), video.getId())
                        .thenAcceptAsync(videoSearch -> {
                            if (playlistSearch instanceof RelationalVideoPlaylist && videoSearch instanceof RelationalVideoInfo) {
                                try (Connection connection = createConnection()) {
                                    insertVideoPlaylistVideo((RelationalVideoPlaylist) playlistSearch, (RelationalVideoInfo) videoSearch, connection);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }));
    }

    @Override
    public CompletableFuture<Void> deleteVideoFromPlaylist(VideoPlaylist playlist, VideoInfo video) {
        return searchPlaylist(playlist.getOwner(), playlist.getName())
                .thenAcceptAsync(playlistSearch -> searchVideoInfo(video.getServiceType(), video.getId())
                        .thenAcceptAsync(videoSearch -> {
                            if (playlistSearch instanceof RelationalVideoPlaylist && videoSearch instanceof RelationalVideoInfo) {
                                try (Connection connection = createConnection()) {
                                    deleteVideoFromPlaylist((RelationalVideoPlaylist) playlistSearch, (RelationalVideoInfo) videoSearch, connection);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }));
    }

    @Nullable
    protected abstract RelationalVideoInfo queryVideoInfo(VideoServiceType videoService, String id, Connection connection) throws SQLException;

    protected abstract int insertVideoInfo(VideoInfo videoInfo, Connection connection) throws SQLException;

    protected abstract void updateVideoInfo(RelationalVideoInfo videoInfo, Connection connection) throws SQLException;

    @Nullable
    protected abstract RelationalVideoRequest queryVideoRequest(UUID requester, RelationalVideoInfo videoInfo, Connection connection) throws SQLException;

    protected abstract void insertVideoRequest(VideoRequest videoRequest, RelationalVideoInfo videoInfo, Connection connection) throws SQLException;

    protected abstract void updateVideoRequest(RelationalVideoRequest videoRequest, Connection connection) throws SQLException;

    protected abstract Set<VideoRequest> queryVideoRequests(UUID requester, Connection connection) throws SQLException;

    @Nullable
    protected abstract RelationalVideoPlaylist queryPlaylist(UUID owner, String name, Connection connection) throws SQLException;

    protected abstract int insertPlaylist(VideoPlaylist playlist, Connection connection) throws SQLException;

    protected abstract void updatePlaylist(RelationalVideoPlaylist playlist, Connection connection) throws SQLException;

    protected abstract void deletePlaylist(RelationalVideoPlaylist playlist, Connection connection) throws SQLException;

    protected abstract void insertPlaylistVideo(RelationalVideoPlaylist playlist, RelationalVideoInfo videoInfo, Connection connection) throws SQLException;

    protected abstract void deletePlaylistVideo(RelationalVideoPlaylist playlist, RelationalVideoInfo videoInfo, Connection connection) throws SQLException;

    protected abstract List<VideoInfo> queryPlaylistVideos(int playlistId, Connection connection) throws SQLException;

    protected abstract List<VideoPlaylist> queryPlaylists(UUID owner, Connection connection) throws SQLException;

    protected abstract void insertVideoPlaylistVideo(RelationalVideoPlaylist playlist, RelationalVideoInfo video, Connection connection) throws SQLException;

    protected abstract void deleteVideoFromPlaylist(RelationalVideoPlaylist playlist, RelationalVideoInfo video, Connection connection) throws SQLException;

    protected abstract void createTables(Connection connection) throws SQLException;

    protected abstract Connection createConnection() throws SQLException;

}
