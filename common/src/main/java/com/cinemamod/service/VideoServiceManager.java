package com.cinemamod.service;

import java.util.HashMap;
import java.util.Map;

public class VideoServiceManager {
    private Map<String, VideoService> registry;

    public VideoServiceManager() {
        registry = new HashMap<>();
    }

    public void register(VideoService videoService) {
        registry.put(videoService.getName(), videoService);
    }

    public void unregisterAll() {
        registry.clear();
    }

    public VideoService getByName(String name) {
        return registry.get(name);
    }
}
