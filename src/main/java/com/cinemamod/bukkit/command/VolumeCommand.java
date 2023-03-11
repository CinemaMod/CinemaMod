package com.cinemamod.bukkit.command;

import com.cinemamod.bukkit.CinemaModPlugin;
import com.cinemamod.bukkit.util.NetworkUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VolumeCommand implements CommandExecutor {

    private final CinemaModPlugin cinemaModPlugin;

    public VolumeCommand(CinemaModPlugin cinemaModPlugin) {
        this.cinemaModPlugin = cinemaModPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        NetworkUtil.sendOpenSettingsScreenPacket(cinemaModPlugin, player);
        return true;
    }

}
