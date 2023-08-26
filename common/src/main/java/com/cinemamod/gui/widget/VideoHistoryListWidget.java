package com.cinemamod.gui.widget;

import com.cinemamod.video.list.VideoList;
import com.cinemamod.video.list.VideoListEntry;
import com.mojang.authlib.minecraft.client.MinecraftClient;

import java.util.List;
import java.util.stream.Collectors;

public class VideoHistoryListWidget extends VideoListWidget {
    public VideoHistoryListWidget(VideoList videoList, MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        super(videoList, client, width, height, top, bottom, itemHeight);
    }

    @Override
    protected List<VideoListWidgetEntry> getWidgetEntries(List<VideoListEntry> entries) {
        return entries.stream()
                .map(entry -> new VideoHistoryListWidgetEntry(this, entry, client))
                .sorted()
                .collect(Collectors.toList());
    }
}
