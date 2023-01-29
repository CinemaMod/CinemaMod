package com.cinemamod.bukkit.storage.sql;

import com.cinemamod.bukkit.video.VideoRequest;

import java.util.UUID;

public class RelationalVideoRequest extends VideoRequest {

    public RelationalVideoRequest(UUID requester, RelationalVideoInfo videoInfo, long lastRequested, int timesRequested, boolean hidden) {
        super(requester, videoInfo, lastRequested, timesRequested, hidden);
    }

    public RelationalVideoRequest(VideoRequest videoRequest, RelationalVideoInfo videoInfo) {
        this(videoRequest.getRequester(), videoInfo, videoRequest.getLastRequested(), videoRequest.getTimesRequested(), videoRequest.isHidden());
    }

    public RelationalVideoInfo getRelationalVideoInfo() {
        return (RelationalVideoInfo) super.getVideoInfo();
    }

}
