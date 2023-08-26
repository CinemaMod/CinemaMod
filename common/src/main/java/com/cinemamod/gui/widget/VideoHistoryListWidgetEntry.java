package com.cinemamod.gui.widget;

import com.cinemamod.util.NetworkUtil;
import com.cinemamod.video.list.VideoListEntry;
import com.mojang.authlib.minecraft.client.MinecraftClient;

public class VideoHistoryListWidgetEntry extends VideoListWidgetEntry {
    public VideoHistoryListWidgetEntry(VideoListWidget parent, VideoListEntry video, MinecraftClient client) {
        super(parent, video, client);
    }

    @Override
    protected void trashButtonAction(VideoListEntry video) {
        NetworkUtil.sendDeleteHistoryPacket(video.getVideoInfo());
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return null;
    }
}
