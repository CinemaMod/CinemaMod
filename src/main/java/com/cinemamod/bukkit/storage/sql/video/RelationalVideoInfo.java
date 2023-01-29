package com.cinemamod.bukkit.storage.sql.video;

import com.cinemamod.bukkit.video.VideoInfo;

public class RelationalVideoInfo extends VideoInfo {

    private final int relId;

    public RelationalVideoInfo(VideoInfo videoInfo, int relId) {
        super(videoInfo.getServiceType(), videoInfo.getId(), videoInfo.getTitle(), videoInfo.getPoster(), videoInfo.getThumbnailUrl(), videoInfo.getDurationSeconds());
        this.relId = relId;
    }

    public int getRelId() {
        return relId;
    }

}
