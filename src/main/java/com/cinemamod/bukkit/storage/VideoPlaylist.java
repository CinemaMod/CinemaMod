package com.cinemamod.bukkit.storage;

import com.cinemamod.bukkit.CinemaModPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VideoPlaylist {

    private UUID owner;
    private String name;
    private Set<VideoInfo> videos;

    public VideoPlaylist(UUID owner, String name, Set<VideoInfo> videos) {
        this.owner = owner;
        this.name = name;
        this.videos = videos;
    }

    public VideoPlaylist(UUID owner, String name) {
        this(owner, name, new HashSet<>());
    }

    public UUID getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name, CinemaModPlugin cinemaModPlugin) {
        this.name = name;
        cinemaModPlugin.getVideoStorage().savePlaylist(this);
    }

    public Set<VideoInfo> getVideos() {
        return videos;
    }

    public void addVideo(VideoInfo videoInfo, CinemaModPlugin cinemaModPlugin) {
        videos.add(videoInfo);
        cinemaModPlugin.getVideoStorage().addVideoToPlaylist(this, videoInfo);
    }

    public void removeVideo(VideoInfo videoInfo, CinemaModPlugin cinemaModPlugin) {
        videos.remove(videoInfo);
        cinemaModPlugin.getVideoStorage().deleteVideoFromPlaylist(this, videoInfo);
    }

    public boolean hasVideo(VideoInfo videoInfo) {
        return videos.contains(videoInfo);
    }

}
