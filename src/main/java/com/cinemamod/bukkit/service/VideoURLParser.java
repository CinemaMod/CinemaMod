package com.cinemamod.bukkit.service;

import com.cinemamod.bukkit.CinemaModPlugin;
import com.cinemamod.bukkit.service.infofetcher.*;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoURLParser {

    private final CinemaModPlugin cinemaModPlugin;
    private final String url;
    private boolean parsed;
    private VideoInfoFetcher infoFetcher;

    public VideoURLParser(CinemaModPlugin cinemaModPlugin, String url) {
        this.cinemaModPlugin = cinemaModPlugin;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void parse(Player player) {
        if (parsed) return;

        parsed = true;

        String youtubeVideoId = getYouTubeVideoId(url);
        if (youtubeVideoId != null) {
            infoFetcher = new YouTubeVideoInfoFetcher(cinemaModPlugin, youtubeVideoId);
            return;
        }

        String twitchUser = getTwitchUser(url);
        if (twitchUser != null) {
            infoFetcher = new TwitchVideoInfoFetcher(twitchUser);
            return;
        }

        if (url.endsWith(".mp4") || url.endsWith(".webm") || url.endsWith("m4v")) {
            infoFetcher = new FileVideoInfoFetcher(cinemaModPlugin, "cinemamod.request.file", url, player == null ? "server" : player.getName());
            return;
        }

        if (url.endsWith(".m3u8")) {
            infoFetcher = new HLSVideoInfoFetcher(url, player == null ? "server" : player.getName());
            return;
        }
    }

    public VideoInfoFetcher getInfoFetcher() {
        return infoFetcher;
    }

    public boolean found() {
        return infoFetcher != null;
    }

    // https://stackoverflow.com/questions/24048308/how-to-get-the-video-id-from-a-youtube-url-with-regex-in-java
    private static String getYouTubeVideoId(String url) {
        String videoId = null;
        String regex = "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            videoId = matcher.group(1);
        }
        return videoId;
    }

    private static String getTwitchUser(String url) {
        String[] parts = url.split("https://www.twitch.tv/");
        if (parts.length > 1) {
            String usernameCandidate = parts[1];
            // Do not allow things like https://www.twitch.tv/xqcow/clip/DirtyCrunchyAxeBloodTrail
            if (!usernameCandidate.contains("/")) {
                return usernameCandidate.trim();
            }
        }
        return null;
    }

}
