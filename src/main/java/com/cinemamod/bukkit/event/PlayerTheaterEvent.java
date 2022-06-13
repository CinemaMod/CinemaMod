package com.cinemamod.bukkit.event;

import com.cinemamod.bukkit.theater.Theater;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerTheaterEvent extends TheaterEvent {

    private static final HandlerList handlers = new HandlerList();

    private Player player;

    public PlayerTheaterEvent(Player player, Theater theater) {
        super(theater);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
