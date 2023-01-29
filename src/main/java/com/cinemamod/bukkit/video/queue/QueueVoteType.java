package com.cinemamod.bukkit.video.queue;

public enum QueueVoteType {

    UP_VOTE(1),
    DOWN_VOTE(-1);

    private final int value;

    QueueVoteType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
