package com.cinemamod.fabric.gui.widget;

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
    private final int bottom;
    @Nullable
    private String search;

    public VideoListWidget(VideoList videoList, MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, itemHeight);
        this.bottom = bottom;
        this.videoList = videoList;
        setRenderHeader(false, 0);
//        setRenderHorizontalShadows(false);
        update();
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.enableScissor(this.getRowLeft(), getY() + 4, this.getScrollbarX() - 8, this.getY() + this.bottom - 80);
        super.renderWidget(context, mouseX, mouseY, delta);
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
