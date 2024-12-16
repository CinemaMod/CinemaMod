package com.cinemamod.bukkit.video;

import com.cinemamod.bukkit.buffer.PacketByteBufReimpl;
import com.cinemamod.bukkit.buffer.PacketByteBufSerializable;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Objects;
import java.util.UUID;

// Serializes to VideoListEntry on client
public class VideoRequest implements PacketByteBufSerializable<VideoRequest> {

    private final UUID requester;
    private final VideoInfo videoInfo;
    private long lastRequested;
    private int timesRequested;
    private boolean hidden;

    public VideoRequest(UUID requester, VideoInfo videoInfo, long lastRequested, int timesRequested, boolean hidden) {
        this.requester = requester;
        this.videoInfo = videoInfo;
        this.lastRequested = lastRequested;
        this.timesRequested = timesRequested;
        this.hidden = hidden;
    }

    public UUID getRequester() {
        return requester;
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public long getLastRequested() {
        return lastRequested;
    }

    public void updateLastRequested() {
        lastRequested = System.currentTimeMillis();
    }

    public int getTimesRequested() {
        return timesRequested;
    }

    public void incrementTimeRequested() {
        timesRequested++;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoRequest request = (VideoRequest) o;
        return Objects.equals(requester, request.requester) && Objects.equals(videoInfo, request.videoInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requester, videoInfo);
    }

    @Override
    public VideoRequest fromBytes(PacketByteBufReimpl buf) {
        throw new NotImplementedException("Not implemented on server");
    }

    @Override
    public void toBytes(PacketByteBufReimpl buf) {
        videoInfo.toBytes(buf);
        buf.writeLong(lastRequested);
        buf.writeInt(timesRequested);
    }

}
