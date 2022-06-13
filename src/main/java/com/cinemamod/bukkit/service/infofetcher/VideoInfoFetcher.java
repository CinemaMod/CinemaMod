package com.cinemamod.bukkit.service.infofetcher;

import com.cinemamod.bukkit.storage.VideoInfo;

import java.util.concurrent.CompletableFuture;

public abstract class VideoInfoFetcher {

    private String permission;

    public VideoInfoFetcher(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    public abstract CompletableFuture<VideoInfo> fetch();

}
