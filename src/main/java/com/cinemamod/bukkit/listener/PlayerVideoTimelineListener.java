package com.cinemamod.bukkit.listener;

import com.cinemamod.bukkit.CinemaModPlugin;
import com.cinemamod.bukkit.theater.StaticTheater;
import com.cinemamod.bukkit.theater.Theater;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class PlayerVideoTimelineListener implements Listener {

    private CinemaModPlugin cinemaModPlugin;

    public PlayerVideoTimelineListener(CinemaModPlugin cinemaModPlugin) {
        this.cinemaModPlugin = cinemaModPlugin;
    }

    @EventHandler
    public void onPlayerChangeItem(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        Theater theater = cinemaModPlugin.getTheaterManager().getCurrentTheater(player);

        if (theater == null || theater instanceof StaticTheater) {
            return;
        }

        if (!theater.isPlaying()) {
            return;
        }

        theater.showBossBars(cinemaModPlugin, player);
    }

}
