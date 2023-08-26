package com.cinemamod.service;

import com.cinemamod.fabric.buffer.PacketByteBufSerializable;
import net.minecraft.network.PacketByteBuf;
import org.apache.commons.lang3.NotImplementedException;

public class VideoService implements PacketByteBufSerializable<VideoService> {
    private String name;
    private String url;
    private String setVolumeJs;
    private String startJs;
    private String seekJs;

    public VideoService(String name, String url, String setVolumeJs, String startJs, String seekJs) {
        this.name = name;
        this.url = url;
        this.setVolumeJs = setVolumeJs;
        this.startJs = startJs;
        this.seekJs = seekJs;
    }

    public VideoService() {

    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getSetVolumeJs() {
        return setVolumeJs;
    }

    public String getStartJs() {
        return startJs;
    }

    public String getSeekJs() {
        return seekJs;
    }

    @Override
    public VideoService fromBytes(PacketByteBuf buf) {
        name = buf.readString();
        url = buf.readString();
        setVolumeJs = buf.readString();
        startJs = buf.readString();
        seekJs = buf.readString();
        return this;
    }

    @Override
    public void toBytes(PacketByteBuf buf) {
        throw new NotImplementedException("Not implemented on client");
    }
}
