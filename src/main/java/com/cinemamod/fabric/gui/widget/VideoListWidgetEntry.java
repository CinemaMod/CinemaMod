package com.cinemamod.fabric.gui.widget;

import com.cinemamod.fabric.CinemaMod;
import com.cinemamod.fabric.util.NetworkUtil;
import com.cinemamod.fabric.video.list.VideoListEntry;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.minecraft.client.gui.DrawableHelper.drawTexture;
import static net.minecraft.client.gui.DrawableHelper.fill;
import static net.minecraft.client.gui.screen.multiplayer.SocialInteractionsPlayerListEntry.GRAY_COLOR;
import static net.minecraft.client.gui.screen.multiplayer.SocialInteractionsPlayerListEntry.WHITE_COLOR;

public abstract class VideoListWidgetEntry extends ElementListWidget.Entry<VideoListWidgetEntry> implements Comparable<VideoListWidgetEntry> {

    private static final Identifier PLAY_TEXTURE = new Identifier(CinemaMod.MODID, "textures/gui/play.png");
    private static final Identifier PLAY_SELECTED_TEXTURE = new Identifier(CinemaMod.MODID, "textures/gui/play_selected.png");
    private static final Identifier TRASH_TEXTURE = new Identifier(CinemaMod.MODID, "textures/gui/trash.png");
    private static final Identifier TRASH_SELECTED_TEXTURE = new Identifier(CinemaMod.MODID, "textures/gui/trash_selected.png");

    private final VideoListWidget parent;
    private final VideoListEntry video;
    private final List<Element> children;
    protected final MinecraftClient client;
    private boolean requestButtonSelected;
    private boolean trashButtonSelected;

    public VideoListWidgetEntry(VideoListWidget parent, VideoListEntry video, MinecraftClient client) {
        this.parent = parent;
        this.video = video;
        children = ImmutableList.of();
        this.client = client;
    }

    public VideoListEntry getVideo() {
        return video;
    }

    @Override
    public List<? extends Element> children() {
        return children;
    }

    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int i = x + 4;
        int j = y + (entryHeight - 24) / 2;
        int m = y + (entryHeight - 7) / 2;
        fill(matrices, x, y, x + entryWidth, y + entryHeight, GRAY_COLOR);
        client.textRenderer.draw(matrices, video.getVideoInfo().getTitleShort(), (float) i, (float) m, WHITE_COLOR);
        client.textRenderer.draw(matrices, String.valueOf(video.getTimesRequested()), (float) i + 160, (float) m, WHITE_COLOR);
        renderRequestButton(matrices, mouseX, mouseY, i, j);
        renderTrashButton(matrices, mouseX, mouseY, i, j);
    }

    @Override
    public int compareTo(@NotNull VideoListWidgetEntry other) {
        return video.compareTo(other.video);
    }

    private void renderRequestButton(MatrixStack matrices, int mouseX, int mouseY, int i, int j) {
        int reqButtonPosX = i + 185;
        int reqButtonY = j + 5;

        requestButtonSelected = mouseX > reqButtonPosX && mouseX < reqButtonPosX + 12 && mouseY > reqButtonY && mouseY < reqButtonY + 12;

        if (requestButtonSelected) {
            RenderSystem.setShaderTexture(0, PLAY_SELECTED_TEXTURE);
        } else {
            RenderSystem.setShaderTexture(0, PLAY_TEXTURE);
        }

        drawTexture(matrices, reqButtonPosX, reqButtonY, 12, 12, 32F, 32F, 8, 8, 8, 8);
    }

    private void renderTrashButton(MatrixStack matrices, int mouseX, int mouseY, int i, int j) {
        int trashButtonPosX = i + 200;
        int trashButtonY = j + 5;

        trashButtonSelected = mouseX > trashButtonPosX && mouseX < trashButtonPosX + 12 && mouseY > trashButtonY && mouseY < trashButtonY + 12;

        if (trashButtonSelected) {
            RenderSystem.setShaderTexture(0, TRASH_SELECTED_TEXTURE);
        } else {
            RenderSystem.setShaderTexture(0, TRASH_TEXTURE);
        }

        drawTexture(matrices, trashButtonPosX, trashButtonY, 12, 12, 32F, 32F, 8, 8, 8, 8);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (requestButtonSelected) {
            NetworkUtil.sendVideoRequestPacket(video.getVideoInfo());
            client.setScreen(null); // close screen
        } else if (trashButtonSelected) {
            trashButtonAction(video);
            parent.videoList.remove(video.getVideoInfo());
        }

        return true;
    }

    protected abstract void trashButtonAction(VideoListEntry video);

}
