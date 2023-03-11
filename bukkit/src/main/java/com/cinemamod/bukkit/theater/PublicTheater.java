package com.cinemamod.bukkit.theater;

import com.cinemamod.bukkit.CinemaModPlugin;
import com.cinemamod.bukkit.theater.screen.Screen;

public class PublicTheater extends Theater {

    public PublicTheater(CinemaModPlugin cinemaModPlugin, String id, String name, boolean hidden, Screen screen) {
        super(cinemaModPlugin, id, name, hidden, screen);
    }

}
