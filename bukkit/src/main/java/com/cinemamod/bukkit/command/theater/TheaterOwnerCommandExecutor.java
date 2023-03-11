package com.cinemamod.bukkit.command.theater;

import com.cinemamod.bukkit.CinemaModPlugin;
import com.cinemamod.bukkit.theater.PrivateTheater;
import com.cinemamod.bukkit.theater.Theater;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public abstract class TheaterOwnerCommandExecutor extends TheaterCommandExecutor {

    public TheaterOwnerCommandExecutor(CinemaModPlugin cinemaModPlugin) {
        super(cinemaModPlugin);
    }

    @Override
    public boolean onTheaterCommand(Player player, Command command, String label, String[] args, Theater theater) {
        boolean hasPermission = false;

        if (player.hasPermission("cinemamod.admin")) {
            hasPermission = true;
        } else if (theater instanceof PrivateTheater) {
            PrivateTheater privateTheater = (PrivateTheater) theater;

            if (privateTheater.hasOwner()) {
                if (privateTheater.getOwner().equals(player)) {
                    hasPermission = true;
                }
            }
        }

        if (hasPermission) {
            return onTheaterOwnerCommand(player, command, label, args, theater);
        } else {
            player.sendMessage(ChatColor.RED + "You must be the owner of the theater to use this command.");
        }

        return true;
    }

    public abstract boolean onTheaterOwnerCommand(Player player, Command command, String label, String[] args, Theater theater);

}
