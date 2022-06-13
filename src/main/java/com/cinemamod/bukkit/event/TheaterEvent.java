package com.cinemamod.bukkit.event;

import com.cinemamod.bukkit.theater.Theater;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TheaterEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Theater theater;

    public TheaterEvent(Theater theater) {
        this.theater = theater;
    }

    public Theater getTheater() {
        return theater;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
