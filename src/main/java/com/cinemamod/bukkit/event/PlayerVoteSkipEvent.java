package com.cinemamod.bukkit.event;

import com.cinemamod.bukkit.theater.Theater;
import org.bukkit.entity.Player;

public class PlayerVoteSkipEvent extends PlayerTheaterEvent {

    public PlayerVoteSkipEvent(Player player, Theater theater) {
        super(player, theater);
    }

}
