package com.cinemamod.bukkit.command.theater;

import com.cinemamod.bukkit.CinemaModPlugin;
import com.cinemamod.bukkit.theater.Theater;
import com.cinemamod.bukkit.util.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class PlayingCommand extends TheaterCommandExecutor {

    public PlayingCommand(CinemaModPlugin cinemaModPlugin) {
        super(cinemaModPlugin);
    }

    @Override
    public boolean onTheaterCommand(Player player, Command command, String label, String[] args, Theater theater) {
        ChatUtil.showPlaying(player, theater, true);
        return true;
    }

}
