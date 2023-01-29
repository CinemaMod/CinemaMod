package com.cinemamod.bukkit.event.queue;

import com.cinemamod.bukkit.theater.Theater;
import com.cinemamod.bukkit.video.Video;

public class TheaterQueueVideoEvent extends TheaterQueueChangeEvent {

    private final Video video;

    public TheaterQueueVideoEvent(Theater theater, Video video) {
        super(theater);
        this.video = video;
    }

    public Video getVideo() {
        return video;
    }

}
