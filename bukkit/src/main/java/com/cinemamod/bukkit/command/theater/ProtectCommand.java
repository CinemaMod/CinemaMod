package com.cinemamod.bukkit.command.theater;

import com.cinemamod.bukkit.CinemaModPlugin;
import com.cinemamod.bukkit.theater.Theater;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class ProtectCommand extends TheaterOwnerCommandExecutor {

    public ProtectCommand(CinemaModPlugin cinemaModPlugin) {
        super(cinemaModPlugin);
    }

    @Override
    public boolean onTheaterOwnerCommand(Player player, Command command, String label, String[] args, Theater theater) {
        // TODO:
        return true;
    }

}
