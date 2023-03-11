package com.cinemamod.bukkit.event;

import com.cinemamod.bukkit.theater.Theater;
import org.bukkit.entity.Player;

public class PlayerLeaveTheaterEvent extends PlayerTheaterEvent {

    public PlayerLeaveTheaterEvent(Player who, Theater theater) {
        super(who, theater);
    }

}
