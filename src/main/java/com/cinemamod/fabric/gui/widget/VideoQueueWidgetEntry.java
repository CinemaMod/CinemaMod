package com.cinemamod.fabric.gui.widget;

import com.cinemamod.fabric.CinemaMod;
import com.cinemamod.fabric.gui.VideoQueueScreen;
import com.cinemamod.fabric.util.NetworkUtil;
import com.cinemamod.fabric.video.queue.QueuedVideo;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.minecraft.client.gui.DrawableHelper.drawTexture;
import static net.minecraft.client.gui.DrawableHelper.fill;
import static net.minecraft.client.gui.screen.multiplayer.SocialInteractionsPlayerListEntry.*;

public class VideoQueueWidgetEntry extends ElementListWidget.Entry<VideoQueueWidgetEntry> implements Comparable<VideoQueueWidgetEntry> {

    private static final Identifier UPVOTE_TEXTURE = new Identifier(CinemaMod.MODID, "textures/gui/upvote.png");
    private static final Identifier UPVOTE_SELECTED_TEXTURE = new Identifier(CinemaMod.MODID, "textures/gui/upvote_selected.png");
    private static final Identifier UPVOTE_ACTIVE_TEXTURE = new Identifier(CinemaMod.MODID, "textures/gui/upvote_active.png");
    private static final Identifier DOWNVOTE_TEXTURE = new Identifier(CinemaMod.MODID, "textures/gui/downvote.png");
    private static final Identifier DOWNVOTE_SELECTED_TEXTURE = new Identifier(CinemaMod.MODID, "textures/gui/downvote_selected.png");
    private static final Identifier DOWNVOTE_ACTIVE_TEXTURE = new Identifier(CinemaMod.MODID, "textures/gui/downvote_active.png");
    private static final Identifier TRASH_TEXTURE = new Identifier(CinemaMod.MODID, "textures/gui/trash.png");
    private static final Identifier TRASH_SELECTED_TEXTURE = new Identifier(CinemaMod.MODID, "textures/gui/trash_selected.png");

    private final VideoQueueScreen parent;
    private final QueuedVideo queuedVideo;
    private final List<Element> children;
    protected MinecraftClient client;
    private boolean downVoteButtonSelected;
    private boolean upVoteButtonSelected;
    private boolean trashButtonSelected;

    public VideoQueueWidgetEntry(VideoQueueScreen parent, QueuedVideo queuedVideo, MinecraftClient client) {
        this.parent = parent;
        this.queuedVideo = queuedVideo;
        children = ImmutableList.of();
        this.client = client;
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
        int color = queuedVideo.isOwner() ? BLACK_COLOR : GRAY_COLOR;
        fill(matrices, x, y, x + entryWidth, y + entryHeight, color);
        client.textRenderer.draw(matrices, queuedVideo.getVideoInfo().getTitleShort(), (float) i, (float) m, WHITE_COLOR);
        client.textRenderer.draw(matrices, queuedVideo.getVideoInfo().getDurationString(), (float) i + 118, (float) m, WHITE_COLOR);
        client.textRenderer.draw(matrices, queuedVideo.getScoreString(), (float) i + 165, (float) m, WHITE_COLOR);
        renderDownVoteButton(matrices, mouseX, mouseY, i, j);
        renderUpVoteButton(matrices, mouseX, mouseY, i, j);
        renderTrashButton(matrices, mouseX, mouseY, i, j);
        if (mouseX > i && mouseX < i + 180 && mouseY > j && mouseY < j + 18) {
            parent.renderTooltip(matrices, Text.of(queuedVideo.getVideoInfo().getTitle()), mouseX, mouseY);
        }
    }

    private void renderDownVoteButton(MatrixStack matrices, int mouseX, int mouseY, int i, int j) {
        int downVoteButtonPosX = i + 185;
        int downVoteButtonPosY = j + 7;

        downVoteButtonSelected = mouseX > downVoteButtonPosX && mouseX < downVoteButtonPosX + 12 && mouseY > downVoteButtonPosY && mouseY < downVoteButtonPosY + 12;

        if (queuedVideo.getClientState() == -1) {
            RenderSystem.setShaderTexture(0, DOWNVOTE_ACTIVE_TEXTURE);
        } else if (downVoteButtonSelected) {
            RenderSystem.setShaderTexture(0, DOWNVOTE_SELECTED_TEXTURE);
        } else {
            RenderSystem.setShaderTexture(0, DOWNVOTE_TEXTURE);
        }

        drawTexture(matrices, downVoteButtonPosX, downVoteButtonPosY, 12, 12, 32F, 32F, 8, 8, 8, 8);
    }

    private void renderUpVoteButton(MatrixStack matrices, int mouseX, int mouseY, int i, int j) {
        int upVoteButtonPosX = i + 200;
        int upVoteButtonPosY = j + 3;

        upVoteButtonSelected = mouseX > upVoteButtonPosX && mouseX < upVoteButtonPosX + 12 && mouseY > upVoteButtonPosY && mouseY < upVoteButtonPosY + 12;

        if (queuedVideo.getClientState() == 1) {
            RenderSystem.setShaderTexture(0, UPVOTE_ACTIVE_TEXTURE);
        } else if (upVoteButtonSelected) {
            RenderSystem.setShaderTexture(0, UPVOTE_SELECTED_TEXTURE);
        } else {
            RenderSystem.setShaderTexture(0, UPVOTE_TEXTURE);
        }

        drawTexture(matrices, upVoteButtonPosX, upVoteButtonPosY, 12, 12, 32F, 32F, 8, 8, 8, 8);
    }

    private void renderTrashButton(MatrixStack matrices, int mouseX, int mouseY, int i, int j) {
        if (queuedVideo.isOwner()) {
            int trashButtonPosX = i + 225;
            int trashButtonPosY = j + 5;

            trashButtonSelected = mouseX > trashButtonPosX && mouseX < trashButtonPosX + 12 && mouseY > trashButtonPosY && mouseY < trashButtonPosY + 12;

            if (trashButtonSelected) {
                RenderSystem.setShaderTexture(0, TRASH_SELECTED_TEXTURE);
            } else {
                RenderSystem.setShaderTexture(0, TRASH_TEXTURE);
            }

            drawTexture(matrices, trashButtonPosX, trashButtonPosY, 12, 12, 32F, 32F, 8, 8, 8, 8);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (downVoteButtonSelected) {
            NetworkUtil.sendVideoQueueVotePacket(queuedVideo.getVideoInfo(), -1);
        } else if (upVoteButtonSelected) {
            NetworkUtil.sendVideoQueueVotePacket(queuedVideo.getVideoInfo(), 1);
        } else if (trashButtonSelected) {
            NetworkUtil.sendVideoQueueRemovePacket(queuedVideo.getVideoInfo());
        }

        return true;
    }

    @Override
    public int compareTo(@NotNull VideoQueueWidgetEntry videoQueueWidgetEntry) {
        return queuedVideo.compareTo(videoQueueWidgetEntry.queuedVideo);
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return null;
    }

}
