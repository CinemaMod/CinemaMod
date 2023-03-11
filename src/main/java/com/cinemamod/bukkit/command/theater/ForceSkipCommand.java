package com.cinemamod.bukkit.command.theater;

import com.cinemamod.bukkit.CinemaModPlugin;
import com.cinemamod.bukkit.theater.Theater;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class ForceSkipCommand extends TheaterOwnerCommandExecutor {

    public ForceSkipCommand(CinemaModPlugin cinemaModPlugin) {
        super(cinemaModPlugin);
    }

    @Override
    public boolean onTheaterOwnerCommand(Player player, Command command, String label, String[] args, Theater theater) {
        if (theater.isPlaying()) {
            theater.forceSkip();
            player.sendMessage(ChatColor.GOLD + "The video has been force skipped.");
        } else {
            player.sendMessage(ChatColor.RED + "This theater is not playing anything.");
        }

        return true;
    }

}
