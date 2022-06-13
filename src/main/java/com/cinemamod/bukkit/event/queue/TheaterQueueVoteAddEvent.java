package com.cinemamod.bukkit.event.queue;

import com.cinemamod.bukkit.theater.Theater;
import com.cinemamod.bukkit.video.queue.QueueVoteType;
import org.bukkit.entity.Player;

public class TheaterQueueVoteAddEvent extends TheaterQueueVoteEvent {

    private QueueVoteType voteType;

    public TheaterQueueVoteAddEvent(Theater theater, Player voter, QueueVoteType voteType) {
        super(theater, voter);
        this.voteType = voteType;
    }

    public QueueVoteType getVoteType() {
        return voteType;
    }

}
