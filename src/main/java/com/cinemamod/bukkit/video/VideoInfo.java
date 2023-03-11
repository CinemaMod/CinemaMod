package com.cinemamod.bukkit.video;

import com.cinemamod.bukkit.buffer.PacketByteBufReimpl;
import com.cinemamod.bukkit.buffer.PacketByteBufSerializable;
import com.cinemamod.bukkit.service.VideoServiceType;

import java.util.Objects;

public class VideoInfo implements PacketByteBufSerializable<VideoInfo> {

    private VideoServiceType serviceType;
    private String id;
    private String title;
    private String poster;
    private String thumbnailUrl;
    private long durationSeconds;

    public VideoInfo(VideoServiceType serviceType, String id, String title, String poster, String thumbnailUrl, long durationSeconds) {
        this.serviceType = serviceType;
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.thumbnailUrl = thumbnailUrl;
        this.durationSeconds = durationSeconds;
    }

    public VideoInfo() {}

    public VideoServiceType getServiceType() {
        return serviceType;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleShort() {
        if (title.length() > 23) {
            return title.substring(0, 20) + "...";
        } else {
            return title;
        }
    }

    public String getPoster() {
        return poster;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    public boolean isLivestream() {
        return durationSeconds == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VideoInfo videoInfo)) {
            return false;
        }
        return serviceType == videoInfo.serviceType && Objects.equals(id, videoInfo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceType, id);
    }

    @Override
    public VideoInfo fromBytes(PacketByteBufReimpl buf) {
        try {
            serviceType = VideoServiceType.valueOf(buf.readString());
        } catch (Exception e) {
            return null;
        }
        id = buf.readString();
        title = buf.readString();
        poster = buf.readString();
        thumbnailUrl = buf.readString();
        durationSeconds = buf.readLong();
        return this;
    }

    @Override
    public void toBytes(PacketByteBufReimpl buf) {
        buf.writeString(serviceType.name());
        buf.writeString(id);
        buf.writeString(title);
        buf.writeString(poster);
        buf.writeString(thumbnailUrl);
        buf.writeLong(durationSeconds);
    }

}
