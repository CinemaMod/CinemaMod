package com.cinemamod.bukkit.event.queue;

import com.cinemamod.bukkit.theater.Theater;
import com.cinemamod.bukkit.video.Video;

public class TheaterQueueVideoAddEvent extends TheaterQueueVideoEvent {

    public TheaterQueueVideoAddEvent(Theater theater, Video video) {
        super(theater, video);
    }

}
