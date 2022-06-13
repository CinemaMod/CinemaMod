package com.cinemamod.bukkit.player;

import com.cinemamod.bukkit.CinemaModPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {

    private CinemaModPlugin cinemaModPlugin;
    private Map<UUID, PlayerData> cache;

    public PlayerDataManager(CinemaModPlugin cinemaModPlugin) {
        this.cinemaModPlugin = cinemaModPlugin;
        cache = new ConcurrentHashMap<>();
    }

    public PlayerData getData(UUID playerId) {
        if (cache.containsKey(playerId)) return cache.get(playerId);
        PlayerData playerData = new PlayerData(playerId);
        cinemaModPlugin.getVideoStorage().loadVideoRequests(playerId).thenAccept(videoRequests -> {
            PlayerRequestHistory requestHistory = new PlayerRequestHistory(playerId, videoRequests);
            playerData.setRequestHistory(requestHistory, cinemaModPlugin);
        });
        cinemaModPlugin.getVideoStorage().loadPlaylists(playerId).thenAccept(videoPlaylists -> {
            playerData.setPlaylists(videoPlaylists, cinemaModPlugin);
        });
        cache.put(playerId, playerData);
        return playerData;
    }

    public void unload(UUID playerId) {
        cache.remove(playerId);
    }

}
