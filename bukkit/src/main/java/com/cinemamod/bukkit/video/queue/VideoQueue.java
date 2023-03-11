package com.cinemamod.bukkit.video.queue;

import com.cinemamod.bukkit.CinemaModPlugin;
import com.cinemamod.bukkit.buffer.PacketByteBufReimpl;
import com.cinemamod.bukkit.event.queue.*;
import com.cinemamod.bukkit.video.VideoInfo;
import com.cinemamod.bukkit.theater.PrivateTheater;
import com.cinemamod.bukkit.theater.Theater;
import com.cinemamod.bukkit.util.NetworkUtil;
import com.cinemamod.bukkit.video.Video;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.PriorityQueue;

public class VideoQueue {

    private static final int MAX_QUEUE_SIZE = 32;

    private final CinemaModPlugin cinemaModPlugin;
    private final Theater theater;
    private final PriorityQueue<Video> priorityQueue;
    private boolean locked;

    public VideoQueue(CinemaModPlugin cinemaModPlugin, Theater theater) {
        this.cinemaModPlugin = cinemaModPlugin;
        this.theater = theater;
        priorityQueue = new PriorityQueue<>();
    }

    public boolean upvoteVideo(Player voter, Video video) {
        return addVote(voter, video, QueueVoteType.UP_VOTE);
    }

    public boolean downvoteVideo(Player voter, Video video) {
        return addVote(voter, video, QueueVoteType.DOWN_VOTE);
    }

    private boolean addVote(Player voter, Video video, QueueVoteType voteType) {
        priorityQueue.remove(video);
        boolean changed = video.addVote(voter, voteType);
        priorityQueue.add(video);
        if (changed) {
            callQueueChangeEvent(new TheaterQueueVoteAddEvent(theater, voter, voteType));
        }
        return changed;
    }

    public boolean removeVote(Player voter, Video video) {
        priorityQueue.remove(video);
        boolean changed = video.removeVote(voter);
        priorityQueue.add(video);
        if (changed) {
            callQueueChangeEvent(new TheaterQueueVoteRemoveEvent(theater, voter));
        }
        return changed;
    }

    public void processPlayerRequest(VideoInfo videoInfo, Player player) {
        cinemaModPlugin.getVideoStorage().saveVideoInfo(videoInfo).thenRun(() -> {
            if (!player.isOnline()) return;

            Video video = new Video(videoInfo, player);
            VideoQueueResult queueResult = queueVideo(video);

            switch (queueResult) {
                case QUEUE_FULL:
                    player.sendMessage(ChatColor.RED + "This theater's queue is full.");
                    break;
                case ALREADY_IN_QUEUE:
                    player.sendMessage(ChatColor.RED + "This video is already in queue.");
                    break;
                case QUEUE_LOCKED:
                    player.sendMessage(ChatColor.RED + "The owner of this theater has locked the queue.");
                    break;
                case SUCCESSFUL:
                    player.sendMessage(ChatColor.GOLD + "Queued video: " + video.getVideoInfo().getTitle());
                    cinemaModPlugin.getPlayerDataManager().getData(player.getUniqueId()).addHistory(videoInfo, cinemaModPlugin);
                    break;
            }
        });
    }

    public VideoQueueResult queueVideo(Video video) {
        if (locked) {
            boolean hasPermission = false;

            if (theater instanceof PrivateTheater) {
                if (video.getRequester().equals(((PrivateTheater) theater).getOwner())) {
                    hasPermission = true;
                }
            }

            if (!hasPermission && video.getRequester().hasPermission("cinemamod.admin")) {
                hasPermission = true;
            }

            if (!hasPermission) {
                return VideoQueueResult.QUEUE_LOCKED;
            }
        }

        for (Video inQueue : priorityQueue) {
            if (inQueue.equals(video)) {
                return VideoQueueResult.ALREADY_IN_QUEUE;
            }
        }

        if (priorityQueue.size() >= MAX_QUEUE_SIZE) {
            return VideoQueueResult.QUEUE_FULL;
        }

        priorityQueue.add(video);

        callQueueChangeEvent(new TheaterQueueVideoAddEvent(theater, video));

        return VideoQueueResult.SUCCESSFUL;
    }

    public void unqueueVideo(Video video) {
        boolean changed = priorityQueue.remove(video);

        if (changed) {
            callQueueChangeEvent(new TheaterQueueVideoRemoveEvent(theater, video));
        }
    }

    public boolean hasNext() {
        return !priorityQueue.isEmpty();
    }

    public Video getVideo(VideoInfo videoInfo) {
        for (Video video : priorityQueue) {
            if (video.getVideoInfo().equals(videoInfo)) {
                return video;
            }
        }
        return null;
    }

    public Video poll() {
        Video video = priorityQueue.poll();
        callQueueChangeEvent(new TheaterQueueVideoRemoveEvent(theater, video));
        return video;
    }

    public void clear() {
        priorityQueue.clear();
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    protected void callQueueChangeEvent(TheaterQueueChangeEvent event) {
        cinemaModPlugin.getServer().getScheduler().runTask(cinemaModPlugin, () -> {
            theater.getViewers().forEach(viewer -> NetworkUtil.sendVideoQueueStatePacket(cinemaModPlugin, viewer, this));
            cinemaModPlugin.getServer().getPluginManager().callEvent(event);
        });
    }

    public void toBytes(PacketByteBufReimpl buf, Player origin) {
        buf.writeInt(priorityQueue.size());
        for (Video video : priorityQueue) {
            video.getVideoInfo().toBytes(buf);
            buf.writeInt(video.getVoteScore());
            final int clientState;
            QueueVoteType currentVote = video.getCurrentVote(origin);
            if (QueueVoteType.UP_VOTE == currentVote) {
                clientState = 1;
            } else if (QueueVoteType.DOWN_VOTE == currentVote) {
                clientState = -1;
            } else {
                clientState = 0;
            }
            buf.writeInt(clientState);
            buf.writeBoolean(origin.getUniqueId().equals(video.getRequester().getUniqueId())); // is owner flag
        }
    }

}
