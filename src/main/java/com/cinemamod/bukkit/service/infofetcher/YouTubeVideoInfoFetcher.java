package com.cinemamod.bukkit.service.infofetcher;

import com.cinemamod.bukkit.CinemaModPlugin;
import com.cinemamod.bukkit.service.VideoServiceType;
import com.cinemamod.bukkit.storage.VideoInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;

public class YouTubeVideoInfoFetcher extends VideoInfoFetcher {

    /*
     *  https://www.googleapis.com/youtube/v3/videos?part=snippet,contentDetails,statistics,status&id={VIDEOID}&key={APIKEY}
     */
    private static final String YOUTUBE_FETCH_URL_FORMAT
            = "https://www.googleapis.com/youtube/v3/videos?part=snippet,contentDetails,statistics,status&id=%s&key=%s";
    private static final JsonParser JSON_PARSER = new JsonParser();

    private CinemaModPlugin cinemaModPlugin;
    private String youtubeDataApiKey;
    private String youtubeVideoId;

    public YouTubeVideoInfoFetcher(CinemaModPlugin cinemaModPlugin, String youtubeVideoId) {
        super("cinemamod.request.youtube");
        this.cinemaModPlugin = cinemaModPlugin;
        this.youtubeDataApiKey = cinemaModPlugin.getCinemaModConfig().youtubeDataApiKey;
        this.youtubeVideoId = youtubeVideoId;
    }

    public boolean keyConfigured() {
        return youtubeDataApiKey.length() == 39;
    }

    @Override
    public CompletableFuture<VideoInfo> fetch() {
        if (!keyConfigured()) {
            cinemaModPlugin.getLogger().warning("A YouTube video was unable to be requested. You must set a YouTube Data API V3 key in your CinemaMod config.yml.");
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                String urlString = String.format(YOUTUBE_FETCH_URL_FORMAT, youtubeVideoId, youtubeDataApiKey);
                URL url = new URL(urlString);

                try (InputStreamReader reader = new InputStreamReader(url.openStream())) {
                    JsonObject root = JSON_PARSER.parse(reader).getAsJsonObject();
                    JsonArray items = root.getAsJsonArray("items");

                    if (items.size() > 0) {
                        String title = items
                                .get(0)
                                .getAsJsonObject()
                                .getAsJsonObject("snippet")
                                .get("title")
                                .getAsString();
                        String thumbnailUrl = items
                                .get(0)
                                .getAsJsonObject()
                                .getAsJsonObject("snippet")
                                .getAsJsonObject("thumbnails")
                                .getAsJsonObject("medium")
                                .get("url")
                                .getAsString();
                        String durationISO8601 = items
                                .get(0)
                                .getAsJsonObject()
                                .getAsJsonObject("contentDetails")
                                .get("duration")
                                .getAsString();
                        Duration duration = Duration.parse(durationISO8601);
                        long durationSeconds = duration.get(ChronoUnit.SECONDS);
                        String channelName = items
                                .get(0)
                                .getAsJsonObject()
                                .getAsJsonObject("snippet")
                                .get("channelTitle")
                                .getAsString();

                        return new VideoInfo(VideoServiceType.YOUTUBE,
                                youtubeVideoId,
                                title,
                                channelName,
                                thumbnailUrl,
                                durationSeconds);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        });
    }

}
