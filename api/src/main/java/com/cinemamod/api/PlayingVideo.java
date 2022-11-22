package com.cinemamod.api;

import java.util.Map;

public interface PlayingVideo extends Video {

    TheaterPlayer getRequester();

    long getStartedAtMillis();

    boolean start();

    boolean hasEnded();

    String getTimeString();

    double getPercentageComplete();

    Map<TheaterPlayer, QueueVoteType> getVotes();

    void addVote(TheaterPlayer player, QueueVoteType type);

    void removeVote(TheaterPlayer player);

    QueueVoteType getCurrentVote(TheaterPlayer player);

    int getVoteScore();

}
