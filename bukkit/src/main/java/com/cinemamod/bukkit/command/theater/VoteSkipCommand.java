package com.cinemamod.bukkit.command.theater;

import com.cinemamod.bukkit.CinemaModPlugin;
import com.cinemamod.bukkit.theater.Theater;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class VoteSkipCommand extends TheaterCommandExecutor {

    public VoteSkipCommand(CinemaModPlugin cinemaModPlugin) {
        super(cinemaModPlugin);
    }

    @Override
    public boolean onTheaterCommand(Player player, Command command, String label, String[] args, Theater theater) {
        if (!theater.isPlaying()) {
            player.sendMessage(ChatColor.RED + "There's no video playing to skip.");
        } else if (!theater.addVoteSkip(player)) {
            player.sendMessage(ChatColor.RED + "You've already voted to skip this video.");
        }

        return true;
    }

}
