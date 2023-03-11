package com.cinemamod.fabric.video.list;

import com.cinemamod.fabric.video.VideoInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VideoList {

    private final Map<VideoInfo, VideoListEntry> videos;

    public VideoList(List<VideoListEntry> videos) {
        this.videos = new HashMap<>();
        videos.forEach(video -> this.videos.put(video.getVideoInfo(), video));
    }

    public VideoList() {
        this(new ArrayList<>());
    }

    public List<VideoListEntry> getVideos() {
        return videos.values().stream().sorted().collect(Collectors.toList());
    }

    public void reset() {
        videos.clear();
    }

    public void merge(VideoList other) {
        other.videos.forEach(this.videos::put);
    }

    public void remove(VideoInfo videoInfo) {
        videos.remove(videoInfo);
    }

}
