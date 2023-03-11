package com.cinemamod.bukkit.event.queue;

import com.cinemamod.bukkit.theater.Theater;
import org.bukkit.entity.Player;

public class TheaterQueueVoteRemoveEvent extends TheaterQueueVoteEvent {

    public TheaterQueueVoteRemoveEvent(Theater theater, Player voter) {
        super(theater, voter);
    }

}
