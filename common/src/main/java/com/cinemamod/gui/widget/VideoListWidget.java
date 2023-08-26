package com.cinemamod.gui.widget;

import com.cinemamod.fabric.video.list.VideoList;
import com.cinemamod.fabric.video.list.VideoListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ElementListWidget;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public abstract class VideoListWidget extends ElementListWidget<VideoListWidgetEntry> {
    protected final VideoList videoList;
    @Nullable
    private String search;

    public VideoListWidget(VideoList videoList, MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
        this.videoList = videoList;
        setRenderBackground(false);
        setRenderHorizontalShadows(false);
        update();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.enableScissor(this.getRowLeft(), this.top + 4, this.getScrollbarPositionX() + this.getRowLeft() + 6, this.height - this.top - 4);
        super.render(context, mouseX, mouseY, delta);
        context.disableScissor();
    }

    public void update() {
        List<VideoListEntry> entries = videoList.getVideos();
        if (search != null)
            entries.removeIf(entry -> !entry.getVideoInfo().getTitle().toLowerCase(Locale.ROOT).contains(search));
        replaceEntries(getWidgetEntries(entries));
    }

    public void setSearch(@Nullable String search) {
        this.search = search;
        update();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        children().forEach(widgetEntry -> widgetEntry.mouseClicked(mouseX, mouseY, button));
        update();
        return true;
    }

    protected abstract List<VideoListWidgetEntry> getWidgetEntries(List<VideoListEntry> entries);
}
