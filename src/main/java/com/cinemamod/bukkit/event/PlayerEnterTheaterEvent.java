package com.cinemamod.bukkit.event;

import com.cinemamod.bukkit.theater.Theater;
import org.bukkit.entity.Player;

public class PlayerEnterTheaterEvent extends PlayerTheaterEvent {

    public PlayerEnterTheaterEvent(Player who, Theater theater) {
        super(who, theater);
    }

}
