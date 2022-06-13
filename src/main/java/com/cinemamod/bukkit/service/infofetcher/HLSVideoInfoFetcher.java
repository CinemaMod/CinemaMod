package com.cinemamod.bukkit.service.infofetcher;

import com.cinemamod.bukkit.service.VideoServiceType;
import com.cinemamod.bukkit.storage.VideoInfo;

import java.util.concurrent.CompletableFuture;

public class HLSVideoInfoFetcher extends VideoInfoFetcher {

    private static final String HLS_THUMBNAIL_URL = "https://cinemamod-static.ewr1.vultrobjects.com/images/hls_thumbnail.jpg";

    private String url;
    private String requesterUsername;

    public HLSVideoInfoFetcher(String url, String requesterUsername) {
        super("cinemamod.request.hls");
        this.url = url;
        this.requesterUsername = requesterUsername;
    }

    @Override
    public CompletableFuture<VideoInfo> fetch() {
        VideoInfo videoInfo = new VideoInfo(
                VideoServiceType.HLS,
                url,
                "HLS Stream",
                requesterUsername,
                HLS_THUMBNAIL_URL,
                0);
        return CompletableFuture.completedFuture(videoInfo);
    }

}
