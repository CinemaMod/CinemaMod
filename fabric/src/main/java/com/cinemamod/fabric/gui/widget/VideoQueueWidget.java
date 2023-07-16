package com.cinemamod.fabric.gui.widget;

import com.cinemamod.fabric.CinemaModClient;
import com.cinemamod.fabric.gui.VideoQueueScreen;
import com.cinemamod.fabric.video.queue.QueuedVideo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ElementListWidget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VideoQueueWidget extends ElementListWidget<VideoQueueWidgetEntry> {

    private VideoQueueScreen parent;

    public VideoQueueWidget(VideoQueueScreen parent, MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
        this.parent = parent;
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

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        children().forEach(child -> child.mouseClicked(mouseX, mouseY, button));
        return true;
    }

    public void update() {
        List<VideoQueueWidgetEntry> entries = new ArrayList<>();
        List<QueuedVideo> queuedVideos = CinemaModClient.getInstance().getVideoQueue().getVideos();
        Collections.sort(queuedVideos);
        for (int i = 0; i < queuedVideos.size(); i++) {
            entries.add(new VideoQueueWidgetEntry(parent, queuedVideos.get(i), client));
        }
        replaceEntries(entries);
    }

}
