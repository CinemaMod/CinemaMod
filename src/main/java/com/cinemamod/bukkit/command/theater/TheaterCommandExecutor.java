package com.cinemamod.bukkit.command.theater;

import com.cinemamod.bukkit.CinemaModPlugin;
import com.cinemamod.bukkit.theater.StaticTheater;
import com.cinemamod.bukkit.theater.Theater;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class TheaterCommandExecutor implements CommandExecutor {

    private CinemaModPlugin cinemaModPlugin;

    public TheaterCommandExecutor(CinemaModPlugin cinemaModPlugin) {
        this.cinemaModPlugin = cinemaModPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        Theater theater = cinemaModPlugin.getTheaterManager().getCurrentTheater(player);

        if (theater == null || theater instanceof StaticTheater) {
            player.sendMessage(ChatColor.RED + "You must be in a theater to use this command.");
            return true;
        }

        return onTheaterCommand(player, command, label, args, theater);
    }

    public abstract boolean onTheaterCommand(Player player, Command command, String label, String[] args, Theater theater);

}
