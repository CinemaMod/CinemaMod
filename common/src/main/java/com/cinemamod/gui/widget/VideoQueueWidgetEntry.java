package com.cinemamod.gui.widget;

import com.cinemamod.fabric.CinemaMod;
import com.cinemamod.fabric.gui.VideoQueueScreen;
import com.cinemamod.fabric.util.NetworkUtil;
import com.cinemamod.fabric.video.queue.QueuedVideo;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int i = x + 4;
        int j = y + (entryHeight - 24) / 2;
        int m = y + (entryHeight - 7) / 2;
        int color = queuedVideo.isOwner() ? BLACK_COLOR : GRAY_COLOR;
        context.fill(x, y, x + entryWidth, y + entryHeight, color);
        context.drawText(client.textRenderer, queuedVideo.getVideoInfo().getTitleShort(), i, m, WHITE_COLOR, false);
        context.drawText(client.textRenderer, queuedVideo.getVideoInfo().getDurationString(), i + 118, m, WHITE_COLOR, false);
        context.drawText(client.textRenderer, queuedVideo.getScoreString(), i + 165, m, WHITE_COLOR, false);
        renderDownVoteButton(context, mouseX, mouseY, i, j);
        renderUpVoteButton(context, mouseX, mouseY, i, j);
        renderTrashButton(context, mouseX, mouseY, i, j);
        if (mouseX > i && mouseX < i + 180 && mouseY > j && mouseY < j + 18) {
            context.drawTooltip(client.textRenderer, Text.of(queuedVideo.getVideoInfo().getTitle()), mouseX, mouseY);
        }
    }

    private void renderDownVoteButton(DrawContext context, int mouseX, int mouseY, int i, int j) {
        int downVoteButtonPosX = i + 185;
        int downVoteButtonPosY = j + 7;

        downVoteButtonSelected = mouseX > downVoteButtonPosX && mouseX < downVoteButtonPosX + 12 && mouseY > downVoteButtonPosY && mouseY < downVoteButtonPosY + 12;

        if (queuedVideo.getClientState() == -1) {
            context.drawTexture(DOWNVOTE_ACTIVE_TEXTURE, downVoteButtonPosX, downVoteButtonPosY, 12, 12, 32F, 32F, 8, 8, 8, 8);
        } else if (downVoteButtonSelected) {
            context.drawTexture(DOWNVOTE_SELECTED_TEXTURE, downVoteButtonPosX, downVoteButtonPosY, 12, 12, 32F, 32F, 8, 8, 8, 8);
        } else {
            context.drawTexture(DOWNVOTE_TEXTURE, downVoteButtonPosX, downVoteButtonPosY, 12, 12, 32F, 32F, 8, 8, 8, 8);
        }
    }

    private void renderUpVoteButton(DrawContext context, int mouseX, int mouseY, int i, int j) {
        int upVoteButtonPosX = i + 200;
        int upVoteButtonPosY = j + 3;

        upVoteButtonSelected = mouseX > upVoteButtonPosX && mouseX < upVoteButtonPosX + 12 && mouseY > upVoteButtonPosY && mouseY < upVoteButtonPosY + 12;

        if (queuedVideo.getClientState() == 1) {
            context.drawTexture(UPVOTE_ACTIVE_TEXTURE, upVoteButtonPosX, upVoteButtonPosY, 12, 12, 32F, 32F, 8, 8, 8, 8);
        } else if (upVoteButtonSelected) {
            context.drawTexture(UPVOTE_SELECTED_TEXTURE, upVoteButtonPosX, upVoteButtonPosY, 12, 12, 32F, 32F, 8, 8, 8, 8);
        } else {
            context.drawTexture(UPVOTE_TEXTURE, upVoteButtonPosX, upVoteButtonPosY, 12, 12, 32F, 32F, 8, 8, 8, 8);
        }
    }

    private void renderTrashButton(DrawContext context, int mouseX, int mouseY, int i, int j) {
        if (queuedVideo.isOwner()) {
            int trashButtonPosX = i + 225;
            int trashButtonPosY = j + 5;

            trashButtonSelected = mouseX > trashButtonPosX && mouseX < trashButtonPosX + 12 && mouseY > trashButtonPosY && mouseY < trashButtonPosY + 12;

            if (trashButtonSelected) {
                context.drawTexture(TRASH_SELECTED_TEXTURE, trashButtonPosX, trashButtonPosY, 12, 12, 32F, 32F, 8, 8, 8, 8);
            } else {
                context.drawTexture(TRASH_TEXTURE, trashButtonPosX, trashButtonPosY, 12, 12, 32F, 32F, 8, 8, 8, 8);
            }
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
