package com.cinemamod.bukkit.player;

import com.cinemamod.bukkit.CinemaModPlugin;
import com.cinemamod.bukkit.video.VideoInfo;
import com.cinemamod.bukkit.video.VideoRequest;
import com.cinemamod.bukkit.util.NetworkUtil;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerData {

    private final UUID playerId;
    private PlayerRequestHistory requestHistory;

    public PlayerData(UUID playerId) {
        this.playerId = playerId;
        requestHistory = new PlayerRequestHistory(playerId);
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

    public List<VideoRequest> getHistoryListEntries() {
        return requestHistory.getRequests().stream()
                .filter(request -> !request.isHidden())
                .collect(Collectors.toList());
    }

}
