package com.cinemamod.bukkit.storage;

import com.cinemamod.bukkit.theater.Theater;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class TheaterStorage {

    public abstract CompletableFuture<Void> saveTheater(Theater theater);

    public abstract CompletableFuture<Set<Theater>> loadTheaters();

}
