package com.cinemamod.bukkit.task;

import com.cinemamod.bukkit.CinemaModPlugin;
import com.cinemamod.bukkit.theater.Theater;
import com.cinemamod.bukkit.util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.StringJoiner;

public class PlayerListUpdateTask implements Runnable {

    private final CinemaModPlugin cinemaModPlugin;

    public PlayerListUpdateTask(CinemaModPlugin cinemaModPlugin) {
        this.cinemaModPlugin = cinemaModPlugin;
    }

    @Override
    public void run() {
        StringJoiner footerBuilder = new StringJoiner("\n");

        footerBuilder.add("");
        footerBuilder.add("");
        footerBuilder.add(ChatColor.BOLD + cinemaModPlugin.getCinemaLanguageConfig().getMessage("now-playing", "NOW PLAYING") + ChatColor.RESET);
        footerBuilder.add(ChatColor.STRIKETHROUGH + "                    " + ChatColor.RESET);

        for (Theater theater : cinemaModPlugin.getTheaterManager().getTheaters()) {
            if (theater.isHidden()) {
                continue;
            }

            final String playing;

            if (!theater.isPlaying()) {
                playing = cinemaModPlugin.getCinemaLanguageConfig().getMessage("nothing", "Nothing");
            } else {
                playing = theater.getPlaying().getVideoInfo().getTitleShort();
            }

            // TODO: is tabular formatting in tab possible?
            String theaterLine = ChatUtil.MAIN_COLOR + theater.getName() + ChatColor.RESET + " || " + ChatUtil.SECONDARY_COLOR + playing;

            footerBuilder.add(theaterLine);
        }

        String footer = footerBuilder.toString();

        for (Player player : cinemaModPlugin.getServer().getOnlinePlayers()) {
            player.setPlayerListFooter(footer);
        }
    }

}
