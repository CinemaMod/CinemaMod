package com.cinemamod.fabric.video.queue;

import com.cinemamod.fabric.video.VideoInfo;
import org.jetbrains.annotations.NotNull;

public class QueuedVideo implements Comparable<QueuedVideo> {

    private final VideoInfo videoInfo;
    private final int score;
    private final int clientState; // -1 = downvote, 0 = no vote, 1 = upvote
    private final boolean owner;

    public QueuedVideo(VideoInfo videoInfo, int score, int clientState, boolean owner) {
        this.videoInfo = videoInfo;
        this.score = score;
        this.clientState = clientState;
        this.owner = owner;
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public int getScore() {
        return score;
    }

    public int getClientState() {
        return clientState;
    }

    public boolean isOwner() {
        return owner;
    }

    public String getScoreString() {
        if (score > 0) {
            return "+" + score;
        } else {
            return String.valueOf(score);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof QueuedVideo)) {
            return false;
        }
        return videoInfo.equals(((QueuedVideo) o).videoInfo);
    }

    @Override
    public int compareTo(@NotNull QueuedVideo queuedVideo) {
        if (score == queuedVideo.score) {
            return 0;
        } else if (score < queuedVideo.score) {
            return 1;
        } else {
            return -1;
        }
    }

}
