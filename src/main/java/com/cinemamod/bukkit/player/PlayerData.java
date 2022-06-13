package com.cinemamod.bukkit.player;

import com.cinemamod.bukkit.CinemaModPlugin;
import com.cinemamod.bukkit.storage.VideoInfo;
import com.cinemamod.bukkit.storage.VideoPlaylist;
import com.cinemamod.bukkit.storage.VideoRequest;
import com.cinemamod.bukkit.util.NetworkUtil;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerData {

    private UUID playerId;
    private PlayerRequestHistory requestHistory;
    private Map<String, VideoPlaylist> playlists;

    public PlayerData(UUID playerId) {
        this.playerId = playerId;
        requestHistory = new PlayerRequestHistory(playerId);
        playlists = new HashMap<>();
    }

    public void setRequestHistory(PlayerRequestHistory requestHistory, CinemaModPlugin cinemaModPlugin) {
        this.requestHistory = requestHistory;
        Player player = cinemaModPlugin.getServer().getPlayer(playerId);
        if (player != null) {
            NetworkUtil.sendVideoListHistorySplitPacket(cinemaModPlugin, player, getHistoryListEntries());
        }
    }

    public void addHistory(VideoInfo videoInfo, CinemaModPlugin cinemaModPlugin) {
        requestHistory.addRequest(videoInfo, cinemaModPlugin);
        VideoRequest request = requestHistory.getRequestFor(videoInfo);
        Player player = cinemaModPlugin.getServer().getPlayer(playerId);
        if (player != null) {
            NetworkUtil.sendVideoListHistorySplitPacket(cinemaModPlugin, player, List.of(request));
        }
    }

    public void deleteHistory(VideoInfo videoInfo, CinemaModPlugin cinemaModPlugin) {
        VideoRequest request = requestHistory.getRequestFor(videoInfo);
        if (request != null) {
            request.setHidden(true);
            cinemaModPlugin.getVideoStorage().saveVideoRequest(request);
        }
    }

    public void setPlaylists(List<VideoPlaylist> playlists, CinemaModPlugin cinemaModPlugin) {
        playlists.forEach(playlist -> this.playlists.put(playlist.getName(), playlist));
        Player player = cinemaModPlugin.getServer().getPlayer(playerId);
        if (player != null) {
            playlists.forEach(playlist -> NetworkUtil.sendVideoListPlaylistSplitPacket(cinemaModPlugin, player, getPlaylistListEntries(playlist), playlist.getName()));
        }
    }

    public void createPlaylist(String name, CinemaModPlugin cinemaModPlugin) {
        VideoPlaylist playlist = new VideoPlaylist(playerId, name);
        playlists.put(playlist.getName(), playlist);
        cinemaModPlugin.getVideoStorage().savePlaylist(playlist);
    }

    public void deletePlaylist(VideoPlaylist playlist, CinemaModPlugin cinemaModPlugin) {
        playlists.remove(playlist.getName());
        cinemaModPlugin.getVideoStorage().deletePlaylist(playlist);
    }

    @Nullable
    public VideoPlaylist getPlaylist(String playlistName) {
        return playlists.get(playlistName);
    }

    public List<VideoRequest> getHistoryListEntries() {
        return requestHistory.getRequests().stream()
                .filter(request -> !request.isHidden())
                .collect(Collectors.toList());
    }

    public List<VideoRequest> getPlaylistListEntries(VideoPlaylist playlist) {
        return requestHistory.getRequests().stream()
                .filter(request -> playlist.hasVideo(request.getVideoInfo()))
                .filter(request -> !request.isHidden())
                .collect(Collectors.toList());
    }

}
