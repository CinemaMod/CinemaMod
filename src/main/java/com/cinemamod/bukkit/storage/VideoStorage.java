package com.cinemamod.bukkit.storage;

import com.cinemamod.bukkit.service.VideoServiceType;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class VideoStorage {

    public abstract CompletableFuture<Void> saveVideoInfo(VideoInfo videoInfo);

    public abstract CompletableFuture<VideoInfo> searchVideoInfo(VideoServiceType videoService, String id);

    public abstract CompletableFuture<Void> saveVideoRequest(VideoRequest request);

    public abstract CompletableFuture<Set<VideoRequest>> loadVideoRequests(UUID requester);

    public abstract CompletableFuture<VideoPlaylist> searchPlaylist(UUID owner, String name);

    public abstract CompletableFuture<Void> savePlaylist(VideoPlaylist playlist);

    public abstract CompletableFuture<Void> deletePlaylist(VideoPlaylist playlist);

    public abstract CompletableFuture<List<VideoPlaylist>> loadPlaylists(UUID owner);

    public abstract CompletableFuture<Void> addVideoToPlaylist(VideoPlaylist playlist, VideoInfo video);

    public abstract CompletableFuture<Void> deleteVideoFromPlaylist(VideoPlaylist playlist, VideoInfo video);

}
