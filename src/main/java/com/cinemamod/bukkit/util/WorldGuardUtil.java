package com.cinemamod.bukkit.util;

import com.cinemamod.bukkit.theater.Theater;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class WorldGuardUtil {

    private static WorldGuard worldGuard;

    public static WorldGuard getWorldGuard() {
        if (worldGuard == null) {
            worldGuard = WorldGuard.getInstance();
        }

        return worldGuard;
    }

    // I define "region map" to be the list of all regions which intersect whatever
    // region is at a location
    public static List<ProtectedRegion> getRegionMap(Location location) {
        BukkitWorld bukkitWorld = new BukkitWorld(location.getWorld());
        RegionManager regionManager = getWorldGuard().getPlatform().getRegionContainer().get(bukkitWorld);
        BlockVector3 blockVector3 = BlockVector3.at(location.getX(), location.getY(), location.getZ());
        ProtectedRegion parent = null;

        for (ProtectedRegion region : regionManager.getApplicableRegions(blockVector3)) {
            if (region instanceof GlobalProtectedRegion) {
                continue;
            }
            parent = region;
            break;
        }

        List<ProtectedRegion> regions = new ArrayList<>();

        if (parent != null) {
            regions.add(parent);
            regions.addAll(parent.getIntersectingRegions(regionManager.getRegions().values()));
        }

        return regions;
    }

    public static List<ProtectedRegion> guessTheaterRegions(Theater theater) {
        Location location = theater.getScreen().getLocation();
        return getRegionMap(location);
    }

    public static Set<Player> getPlayersInRegion(ProtectedRegion region) {
        Set<Player> players = new HashSet<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            Location location = player.getLocation();
            if (region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
                players.add(player);
            }
        }
        return players;
    }

}
