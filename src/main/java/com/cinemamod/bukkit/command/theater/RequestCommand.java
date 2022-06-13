package com.cinemamod.bukkit.command.theater;

import com.cinemamod.bukkit.CinemaModPlugin;
import com.cinemamod.bukkit.service.VideoURLParser;
import com.cinemamod.bukkit.theater.Theater;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class RequestCommand extends TheaterCommandExecutor {

    private CinemaModPlugin cinemaModPlugin;
    private Set<Player> lock;

    public RequestCommand(CinemaModPlugin cinemaModPlugin) {
        super(cinemaModPlugin);
        this.cinemaModPlugin = cinemaModPlugin;
        lock = new HashSet<>();
    }

    @Override
    public boolean onTheaterCommand(Player player, Command command, String label, String[] args, Theater theater) {
        if (lock.contains(player)) {
            player.sendMessage(ChatColor.RED + "Wait to use this command again.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Invalid URL. /" + label + " <url>");
            player.sendMessage(ChatColor.RED + "Example: /" + label + " https://www.youtube.com/watch?v=dQw4w9WgXcQ");
            return true;
        }

        String url = args[0];
        VideoURLParser parser = new VideoURLParser(cinemaModPlugin, url);

        parser.parse(player);

        if (!parser.found()) {
            player.sendMessage(ChatColor.RED + "This URL or video type is not supported.");
            return true;
        }

        if (!player.hasPermission(parser.getInfoFetcher().getPermission())) {
            player.sendMessage(ChatColor.RED + "You do not have permission to request this type of video.");
            return true;
        }

        player.sendMessage(ChatColor.GOLD + "Fetching video information...");

        lock.add(player);

        parser.getInfoFetcher().fetch().thenAccept(videoInfo -> {
            lock.remove(player);

            if (!player.isOnline()) return;

            if (!theater.isViewer(player)) {
                player.sendMessage(ChatColor.RED + "The video you requested was not queued because you left the theater.");
                return;
            }

            if (videoInfo == null) {
                player.sendMessage(ChatColor.RED + "Unable to fetch video information.");
                return;
            }

            theater.getVideoQueue().processPlayerRequest(videoInfo, player);
        });

        return true;
    }

}
