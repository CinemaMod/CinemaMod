package com.cinemamod.bukkit.event.queue;

import com.cinemamod.bukkit.theater.Theater;
import org.bukkit.entity.Player;

public class TheaterQueueVoteEvent extends TheaterQueueChangeEvent {

    private Player voter;

    public TheaterQueueVoteEvent(Theater theater, Player voter) {
        super(theater);
        this.voter = voter;
    }

    public Player getVoter() {
        return voter;
    }

}
