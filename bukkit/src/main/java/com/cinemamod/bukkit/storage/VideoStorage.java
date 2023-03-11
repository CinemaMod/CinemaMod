package com.cinemamod.bukkit.storage;

import com.cinemamod.bukkit.service.VideoServiceType;
import com.cinemamod.bukkit.video.VideoInfo;
import com.cinemamod.bukkit.video.VideoRequest;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface VideoStorage {

    CompletableFuture<Void> saveVideoInfo(VideoInfo videoInfo);

    CompletableFuture<VideoInfo> searchVideoInfo(VideoServiceType videoService, String id);

    CompletableFuture<Void> saveVideoRequest(VideoRequest request);

    CompletableFuture<Set<VideoRequest>> loadVideoRequests(UUID requester);

}
