package com.cinemamod.bukkit.storage.sql;

import com.cinemamod.bukkit.storage.VideoPlaylist;

public class RelationalVideoPlaylist extends VideoPlaylist {

    private int relId;

    public RelationalVideoPlaylist(VideoPlaylist playlist, int relId) {
        super(playlist.getOwner(), playlist.getName(), playlist.getVideos());
        this.relId = relId;
    }

    public int getRelId() {
        return relId;
    }

}
