package com.cinemamod.fabric.video;

import com.cinemamod.fabric.CinemaModClient;
import com.cinemamod.fabric.service.VideoService;
import net.minecraft.network.PacketByteBuf;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class VideoInfo {

    private VideoService videoService;
    private String id;
    private String title;
    private String poster;
    private String thumbnailUrl;
    private long durationSeconds;

    public VideoInfo(VideoService videoService, String id) {
        this.videoService = videoService;
        this.id = id;
    }

    public VideoInfo() {

    }

    public VideoService getVideoService() {
        return videoService;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(@Nullable String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getDurationString() {
        long totalDurationMillis = durationSeconds * 1000;
        String totalDurationFormatted = DurationFormatUtils.formatDuration(totalDurationMillis, "H:mm:ss");
        totalDurationFormatted = reduceFormattedDuration(totalDurationFormatted);
        return totalDurationFormatted;
    }

    private static String reduceFormattedDuration(String formatted) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] split = formatted.split(":");

        // If does not have hours
        if (!split[0].equals("0")) {
            return formatted;
        } else {
            stringBuilder.append(split[1]).append(":").append(split[2]);
            return stringBuilder.toString();
        }
    }

    public boolean isLivestream() {
        return durationSeconds == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VideoInfo)) {
            return false;
        }
        VideoInfo videoInfo = (VideoInfo) o;
        return videoService == videoInfo.videoService && Objects.equals(id, videoInfo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(videoService, id);
    }

    public VideoInfo fromBytes(PacketByteBuf buf) {
        videoService = CinemaModClient.getInstance().getVideoServiceManager().getByName(buf.readString());
        if (videoService == null) return null;
        id = buf.readString();
        title = buf.readString();
        poster = buf.readString();
        thumbnailUrl = buf.readString();
        durationSeconds = buf.readLong();
        return this;
    }

    public void toBytes(PacketByteBuf buf) {
        buf.writeString(videoService.getName());
        buf.writeString(id);
        buf.writeString(title);
        buf.writeString(poster);
        buf.writeString(thumbnailUrl);
        buf.writeLong(durationSeconds);
    }

}
