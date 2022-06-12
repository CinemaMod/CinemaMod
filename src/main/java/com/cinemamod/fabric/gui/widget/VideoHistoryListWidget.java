package com.cinemamod.fabric.gui.widget;

import com.cinemamod.fabric.video.list.VideoList;
import com.cinemamod.fabric.video.list.VideoListEntry;
import net.minecraft.client.MinecraftClient;

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
