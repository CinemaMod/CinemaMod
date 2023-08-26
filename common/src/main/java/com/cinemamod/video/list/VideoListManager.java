package com.cinemamod.video.list;

import java.util.HashMap;
import java.util.Map;

public class VideoListManager {
    private VideoList history;
    private Map<String, VideoList> playlists;

    public VideoListManager() {
        history = new VideoList();
        playlists = new HashMap<>();
    }

    public VideoList getHistory() {
        return history;
    }

    public VideoList getPlaylist(String name) {
        return playlists.get(name);
    }

    public void reset() {
        history.reset();
        playlists.forEach((s, videoList) -> videoList.reset());
    }
}
