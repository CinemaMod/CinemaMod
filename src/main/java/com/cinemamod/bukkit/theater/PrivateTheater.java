package com.cinemamod.bukkit.theater;

import com.cinemamod.bukkit.CinemaModPlugin;
import com.cinemamod.bukkit.event.TheaterSetOwnerEvent;
import com.cinemamod.bukkit.theater.screen.Screen;
import org.bukkit.entity.Player;

public class PrivateTheater extends Theater {

    private Player owner;
    private long protectionUntil;

    public PrivateTheater(CinemaModPlugin cinemaModPlugin, String id, String name, boolean hidden, Screen screen) {
        super(cinemaModPlugin, id, name, hidden, screen);
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public boolean isOwner(Player player) {
        return player.equals(owner);
    }

    public boolean hasOwner() {
        return owner != null;
    }

    public boolean isProtected() {
        return protectionUntil > System.currentTimeMillis();
    }

    public void setProtectionUntil(long protectionUntil) {
        this.protectionUntil = protectionUntil;
    }

    public void reset() {
        super.reset();
        owner = null;
        protectionUntil = 0;
    }

    @Override
    public void tick(CinemaModPlugin cinemaModPlugin) {
        if (isPlaying()) {
            if (getViewers().isEmpty()) {
                forceSkip();
            }
        }

        super.tick(cinemaModPlugin);

        if (owner != null) {
            // Check if the owner is offline or is no longer a viewer
            if (!owner.isOnline() || !isViewer(owner)) {
                owner = null;
                protectionUntil = 0;
                getVideoQueue().setLocked(false);
            }
        }

        if (owner == null) {
            if (getVideoQueue().isLocked()) {
                getVideoQueue().setLocked(false);
            }
        }

        if (owner == null && isPlaying() && isViewer(getPlaying().getRequester())) {
            owner = getPlaying().getRequester();
            TheaterSetOwnerEvent event = new TheaterSetOwnerEvent(owner, this);
            cinemaModPlugin.getServer().getPluginManager().callEvent(event);
        }
    }

}
