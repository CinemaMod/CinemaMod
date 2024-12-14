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
    private int bottom;

    public VideoQueueWidget(VideoQueueScreen parent, MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, itemHeight);
        this.parent = parent;
        this.bottom = bottom;
        //setRenderHeader(false, 0);
//        setRenderHorizontalShadows(false);
        update();
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.enableScissor(this.getRowLeft(), getY() + 4, this.getScrollbarX() - 8, this.getY() + this.bottom - 70);
        super.renderWidget(context, mouseX, mouseY, delta);
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
