package com.cinemamod.bukkit.service.infofetcher;

import com.cinemamod.bukkit.video.VideoInfo;

import java.util.concurrent.CompletableFuture;

public abstract class VideoInfoFetcher {

    private final String permission;

    public VideoInfoFetcher(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    public abstract CompletableFuture<VideoInfo> fetch();

}
