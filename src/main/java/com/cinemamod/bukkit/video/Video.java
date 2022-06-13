package com.cinemamod.bukkit.video;

import com.cinemamod.bukkit.buffer.PacketByteBufReimpl;
import com.cinemamod.bukkit.buffer.PacketByteBufSerializable;
import com.cinemamod.bukkit.storage.VideoInfo;
import com.cinemamod.bukkit.video.queue.QueueVoteType;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Video implements Comparable<Video>, PacketByteBufSerializable<Video> {

    private VideoInfo videoInfo;
    private long startedAt;
    private transient Player requester;
    private transient Map<Player, QueueVoteType> votes;

    public Video(VideoInfo videoInfo, Player requester) {
        this.videoInfo = videoInfo;
        this.requester = requester;
        votes = new HashMap<>();
        votes.put(requester, QueueVoteType.UPVOTE);
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public Player getRequester() {
        return requester;
    }

    public boolean addVote(Player player, QueueVoteType voteType) {
        QueueVoteType previous = votes.put(player, voteType);
        return previous == null || previous != voteType; // false if nothing changed
    }

    public boolean removeVote(Player player) {
        return votes.remove(player) != null;
    }

    public int getVoteScore() {
        int score = 0;
        for (Player voter : votes.keySet()) {
            QueueVoteType voteType = votes.get(voter);
            switch (voteType) {
                case UPVOTE:
                    score++;
                    break;
                case DOWNVOTE:
                    score--;
                    break;
            }
        }
        return score;
    }

    public QueueVoteType getCurrentVote(Player player) {
        return votes.get(player);
    }

    public void start() {
        startedAt = System.currentTimeMillis();
    }

    public long getStartedAt() {
        return startedAt;
    }

    public boolean hasEnded() {
        if (videoInfo.isLivestream()) {
            return false;
        } else {
            return System.currentTimeMillis() > (startedAt + (videoInfo.getDurationSeconds() * 1000));
        }
    }

    public String getTimeString() {
        long currentDurationMillis = System.currentTimeMillis() - startedAt;
        long totalDurationMillis = videoInfo.getDurationSeconds() * 1000;
        StringBuilder stringBuilder = new StringBuilder();

        String currentDurationFormatted = DurationFormatUtils.formatDuration(currentDurationMillis, "H:mm:ss");
        String totalDurationFormatted = DurationFormatUtils.formatDuration(totalDurationMillis, "H:mm:ss");

        currentDurationFormatted = reduceFormattedDuration(currentDurationFormatted);
        totalDurationFormatted = reduceFormattedDuration(totalDurationFormatted);

        stringBuilder.append(currentDurationFormatted);
        stringBuilder.append(" / ");
        stringBuilder.append(totalDurationFormatted);

        return stringBuilder.toString();
    }

    private static String reduceFormattedDuration(String formatted) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] split = formatted.split(":");

        // If does not have hours
        if (!split[0].equals("0")) {
            return formatted;
        } else {
            stringBuilder.append(split[1]).append(":").append(split[2]);
            return stringBuilder.toString();
        }
    }

    public double getPercentageComplete() {
        double currentDurationMillis = (double) (System.currentTimeMillis() - startedAt);
        double totalDurationMillis = videoInfo.getDurationSeconds() * 1000.0;
        return (currentDurationMillis / totalDurationMillis);
    }

    // Used for PriorityQueue sorting
    @Override
    public int compareTo(Video other) {
        int thisScore = getVoteScore();
        int otherScore = other.getVoteScore();

        if (thisScore < otherScore) {
            return 1;
        } else if (thisScore > otherScore) {
            return -1;
        } else {
            // TODO: check request time
            return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Video)) {
            return false;
        }
        Video video = (Video) o;
        return video.getVideoInfo().equals(videoInfo);
    }

    @Override
    public Video fromBytes(PacketByteBufReimpl buf) {
        throw new NotImplementedException("Not implemented on server");
    }

    @Override
    public void toBytes(PacketByteBufReimpl buf) {
        videoInfo.toBytes(buf);
        buf.writeLong(startedAt);
    }

}
