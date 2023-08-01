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
        String hlsLink = getLink(url, "\\.(m3u(8|))(?=\\??)", 0);
        if (hlsLink != null) {
            infoFetcher = new HLSVideoInfoFetcher(url, player == null ? "server" : player.getName());
            return;
        }

        String mediaLink = getLink(url, "\\.((mp(3|4|eg))|ts|webm|m4v|ogg|opus)(?=\\??)", 0);
        if (if mediaLink != null) {
            infoFetcher = new FileVideoInfoFetcher("cinemamod.request.file", url, player == null ? "server" : player.getName());
            return;
        }

    }
    private static String getLink(String url, String regex, int group) {
        Static link = null;
        Pattern ptn = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = ptn.matcher(url);
        if (matcher.find()) { link = matcher.group(group); }
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
