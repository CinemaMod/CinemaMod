package com.cinemamod.bukkit.storage;

import com.cinemamod.bukkit.service.VideoServiceType;
import com.cinemamod.bukkit.video.VideoInfo;
import com.cinemamod.bukkit.video.VideoRequest;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class VideoStorage {

    public abstract CompletableFuture<Void> saveVideoInfo(VideoInfo videoInfo);

    public abstract CompletableFuture<VideoInfo> searchVideoInfo(VideoServiceType videoService, String id);

    public abstract CompletableFuture<Void> saveVideoRequest(VideoRequest request);

    public abstract CompletableFuture<Set<VideoRequest>> loadVideoRequests(UUID requester);

}
