package com.cinemamod.bukkit.command.theater;

import com.cinemamod.bukkit.CinemaModPlugin;
import com.cinemamod.bukkit.theater.Theater;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class LockQueueCommand extends TheaterOwnerCommandExecutor {

    public LockQueueCommand(CinemaModPlugin cinemaModPlugin) {
        super(cinemaModPlugin);
    }

    @Override
    public boolean onTheaterOwnerCommand(Player player, Command command, String label, String[] args, Theater theater) {
        boolean wasLocked = theater.getVideoQueue().isLocked();
        theater.getVideoQueue().setLocked(!wasLocked);

        if (wasLocked) {
            player.sendMessage(ChatColor.GOLD + "The video queue is now unlocked.");
        } else {
            player.sendMessage(ChatColor.GOLD + "The video queue is now locked.");
        }

        return true;
    }

}
