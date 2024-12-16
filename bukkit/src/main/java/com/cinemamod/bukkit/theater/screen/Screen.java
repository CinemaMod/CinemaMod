package com.cinemamod.bukkit.theater.screen;

import com.cinemamod.bukkit.buffer.PacketByteBufReimpl;
import com.cinemamod.bukkit.buffer.PacketByteBufSerializable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.apache.commons.lang3.NotImplementedException;

public class Screen implements PacketByteBufSerializable<Screen> {

    private String world;
    private int x;
    private int y;
    private int z;
    private String facing;
    private float width;
    private float height;
    private boolean visible;
    private boolean muted;

    public Screen(String world, int x, int y, int z, String facing, float width, float height, boolean visible, boolean muted) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.facing = facing;
        this.width = width;
        this.height = height;
        this.visible = visible;
        this.muted = muted;
    }

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public String getFacing() {
        return facing;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isMuted() {
        return muted;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    @Override
    public Screen fromBytes(PacketByteBufReimpl buf) {
        throw new NotImplementedException("Not implemented on server");
    }

    @Override
    public void toBytes(PacketByteBufReimpl buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeString(facing);
        buf.writeFloat(width);
        buf.writeFloat(height);
        buf.writeBoolean(visible);
        buf.writeBoolean(muted);
    }

}
