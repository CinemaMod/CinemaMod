package com.cinemamod.bukkit.storage;

import com.cinemamod.bukkit.theater.Theater;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface TheaterStorage {

    CompletableFuture<Void> saveTheater(Theater theater);

    CompletableFuture<Set<Theater>> loadTheaters();

}
