package com.cinemamod.bukkit.service.infofetcher;

import com.cinemamod.bukkit.CinemaModPlugin;
import com.cinemamod.bukkit.service.VideoServiceType;
import com.cinemamod.bukkit.video.VideoInfo;
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
    private static final String PIPED_FETCH_URL_FORMAT = "https://pipedapi.kavin.rocks/streams/%s";

    private final CinemaModPlugin cinemaModPlugin;
    private final String youtubeDataApiKey;
    private final String youtubeVideoId;

    public YouTubeVideoInfoFetcher(CinemaModPlugin cinemaModPlugin, String youtubeVideoId) {
        super("cinemamod.request.youtube");
        this.cinemaModPlugin = cinemaModPlugin;
        this.youtubeDataApiKey = cinemaModPlugin.getCinemaModConfig().youtubeDataApiKey;
        this.youtubeVideoId = youtubeVideoId;
    }

    public boolean keyConfigured() {
        return youtubeDataApiKey.length() == 39;
    }

    private CompletableFuture<VideoInfo> fetchPiped() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String urlString = String.format(PIPED_FETCH_URL_FORMAT, youtubeVideoId);
                URL url = new URL(urlString);

                try (InputStreamReader reader = new InputStreamReader(url.openStream())) {
                    JsonObject root = JSON_PARSER.parse(reader).getAsJsonObject();
                    return new VideoInfo(VideoServiceType.YOUTUBE,
                            youtubeVideoId,
                            root.get("title").getAsString(),
                            root.get("uploader").getAsString(),
                            root.get("thumbnailUrl").getAsString(), // goes through imageproxy tho
                            root.get("duration").getAsInt());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public CompletableFuture<VideoInfo> fetch() {
        // FUTURE TODO: Once another yt backend is added, allow for selection via "youtube-backend" config option instead of autoselecting based off if an valid api key is provided. 
        if (!keyConfigured()) {
            cinemaModPlugin.getLogger().warning("A YouTube video was unable to be requested. You must set a YouTube Data API V3 key in your CinemaMod config.yml. Falling back to Piped and Invidious backends");
            // Piped only for now (for some reason I feel like it's more reliable)
            return fetchPiped();
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
