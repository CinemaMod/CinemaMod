package com.cinemamod.bukkit.service;

import com.cinemamod.bukkit.CinemaModPlugin;
import com.cinemamod.bukkit.service.infofetcher.*;
import org.bukkit.entity.Player;
import java.net.URL;
import java.io.IOException;

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

        String youtubeVideoId = getLink(url,
        // https://stackoverflow.com/questions/24048308/how-to-get-the-video-id-from-a-youtube-url-with-regex-in-java
        "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)",
        1);
        if (youtubeVideoId != null) {
            infoFetcher = new YouTubeVideoInfoFetcher(cinemaModPlugin, youtubeVideoId);
            return;
        }

        String twitchUser = getTwitchUser(url);
        if (twitchUser != null) {
            infoFetcher = new TwitchVideoInfoFetcher(twitchUser);
            return;
        }
        String hlsLink = getLink(url, "^https?:\\/\\/[\\S\\-]+(\\.[\\S\\-]+)*\\/[\\S\\-]+\\.(m3u8)(.*)?$", 0);
        if (hlsLink != null) {
            infoFetcher = new HLSVideoInfoFetcher(url, player == null ? "server" : player.getName());
            return;
        }

        String mediaLink = getLink(url, "^https?:\\/\\/[\\W\\w\\-]+(\\.[\\W\\w\\-]+)*\\/[\\W\\w\\-]+\\.(mp[3-4]|ts|ogg|opus|webm|m4v)(.*)?$", 0);
        if (mediaLink != null) {
            infoFetcher = new FileVideoInfoFetcher("cinemamod.request.file", url, player == null ? "server" : player.getName());
            return;
        }

    }
    private static String getLink(String url, String regex, int group) {
        String link = null;
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) { 
            if (group != 0) {
                link = matcher.group(group); 
            }
            else {
                Pattern mimePattern = Pattern.compile(
                    "(video|audio|application)\\/(mp4|mp3|opus|mp2t|wav|webm|mkv|mpegurl|vnd.apple.mpegurl)", 
                    Pattern.CASE_INSENSITIVE);
                try {
                    String contentTypeResponse = new URL(url).openConnection().getContentType();
                    Matcher mimeMatcher = mimePattern.matcher(contentTypeResponse);
                    if (mimeMatcher.find()) {
                        link = matcher.group(group); 
                    }
                } catch (IOException ignored) {}
            }
        }
        return link;
    }
    public VideoInfoFetcher getInfoFetcher() {
        return infoFetcher;
    }

    public boolean found() {
        return infoFetcher != null;
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
