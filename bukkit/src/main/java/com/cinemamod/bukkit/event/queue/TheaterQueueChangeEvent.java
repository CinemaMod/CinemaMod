package com.cinemamod.bukkit.event.queue;

import com.cinemamod.bukkit.event.TheaterEvent;
import com.cinemamod.bukkit.theater.Theater;

public class TheaterQueueChangeEvent extends TheaterEvent {

    public TheaterQueueChangeEvent(Theater theater) {
        super(theater);
    }

}
