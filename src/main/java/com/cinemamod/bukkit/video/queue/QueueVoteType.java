package com.cinemamod.bukkit.video.queue;

public enum QueueVoteType {

    UPVOTE(1),
    DOWNVOTE(-1);

    private int value;

    QueueVoteType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
