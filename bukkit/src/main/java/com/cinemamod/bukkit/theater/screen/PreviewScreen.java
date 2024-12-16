package com.cinemamod.bukkit.theater.screen;

import com.cinemamod.bukkit.buffer.PacketByteBufReimpl;
import com.cinemamod.bukkit.buffer.PacketByteBufSerializable;
import org.apache.commons.lang3.NotImplementedException;

public class PreviewScreen implements PacketByteBufSerializable<PreviewScreen> {

    private String world;
    private int x;
    private int y;
    private int z;
    private String facing;
    private String staticTextureUrl; // When nothing's playing
    private String activeTextureUrl; // When something's playing

    public PreviewScreen(String world, int x, int y, int z, String facing) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.facing = facing;
        staticTextureUrl = "https://cinemamod-static.ewr1.vultrobjects.com/images/flatscreen_bars.jpg";
        activeTextureUrl = "https://cinemamod-static.ewr1.vultrobjects.com/images/flatscreen_bg1.jpg";
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

    public String getStaticTextureUrl() {
        return staticTextureUrl;
    }

    public String getActiveTextureUrl() {
        return activeTextureUrl;
    }

    @Override
    public PreviewScreen fromBytes(PacketByteBufReimpl buf) {
        throw new NotImplementedException("Not implemented on server");
    }

    @Override
    public void toBytes(PacketByteBufReimpl buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeString(facing);
        buf.writeString(staticTextureUrl);
        buf.writeString(activeTextureUrl);
    }

}
