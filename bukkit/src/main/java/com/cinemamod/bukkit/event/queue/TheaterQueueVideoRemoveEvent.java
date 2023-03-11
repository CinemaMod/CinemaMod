package com.cinemamod.bukkit.event.queue;

import com.cinemamod.bukkit.theater.Theater;
import com.cinemamod.bukkit.video.Video;

public class TheaterQueueVideoRemoveEvent extends TheaterQueueVideoEvent {

    public TheaterQueueVideoRemoveEvent(Theater theater, Video video) {
        super(theater, video);
    }

}
