package com.cinemamod.fabric.gui.widget;

import com.cinemamod.fabric.CinemaMod;
import com.cinemamod.fabric.util.NetworkUtil;
import com.cinemamod.fabric.video.list.VideoListEntry;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

import static net.minecraft.client.gui.screen.multiplayer.SocialInteractionsPlayerListEntry.GRAY_COLOR;
import static net.minecraft.client.gui.screen.multiplayer.SocialInteractionsPlayerListEntry.WHITE_COLOR;
import static net.minecraft.client.render.RenderPhase.*;
import static net.minecraft.client.render.RenderPhase.COLOR_MASK;

public abstract class VideoListWidgetEntry extends ElementListWidget.Entry<VideoListWidgetEntry> implements Comparable<VideoListWidgetEntry> {

    private static final Identifier PLAY_TEXTURE = Identifier.of(CinemaMod.MODID, "textures/gui/play.png");
    private static final Identifier PLAY_SELECTED_TEXTURE = Identifier.of(CinemaMod.MODID, "textures/gui/play_selected.png");
    private static final Identifier TRASH_TEXTURE = Identifier.of(CinemaMod.MODID, "textures/gui/trash.png");
    private static final Identifier TRASH_SELECTED_TEXTURE = Identifier.of(CinemaMod.MODID, "textures/gui/trash_selected.png");

    private final VideoListWidget parent;
    private final VideoListEntry video;
    private final List<Element> children;
    protected final MinecraftClient client;
    private boolean requestButtonSelected;
    private boolean trashButtonSelected;

    private final Function<Identifier, RenderLayer> GUI_TEXTURED = Util.memoize((texture) -> {
        return RenderLayer.of("gui_textured_overlay", VertexFormats.POSITION_TEXTURE_COLOR, VertexFormat.DrawMode.QUADS, 1536, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, TriState.DEFAULT, false)).program(POSITION_TEXTURE_COLOR_PROGRAM).transparency(TRANSLUCENT_TRANSPARENCY).depthTest(ALWAYS_DEPTH_TEST).writeMaskState(COLOR_MASK).build(false));
    });

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
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int i = x + 4;
        int j = y + (entryHeight - 24) / 2;
        int m = y + (entryHeight - 7) / 2;
        context.fill(x, y, x + entryWidth, y + entryHeight, GRAY_COLOR);
        context.drawText(client.textRenderer, video.getVideoInfo().getTitleShort(), i, m, WHITE_COLOR, false);
        context.drawText(client.textRenderer, String.valueOf(video.getTimesRequested()),  i + 160, m, WHITE_COLOR, false);
        renderRequestButton(context, mouseX, mouseY, i, j);
        renderTrashButton(context, mouseX, mouseY, i, j);
    }

    @Override
    public int compareTo(@NotNull VideoListWidgetEntry other) {
        return video.compareTo(other.video);
    }

    private void renderRequestButton(DrawContext context, int mouseX, int mouseY, int i, int j) {
        Function<Identifier, RenderLayer> GUI_TEXTURED = Util.memoize((texture) -> {
            return RenderLayer.of("gui_textured_overlay", VertexFormats.POSITION_TEXTURE_COLOR, VertexFormat.DrawMode.QUADS, 1536, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, TriState.DEFAULT, false)).program(POSITION_TEXTURE_COLOR_PROGRAM).transparency(TRANSLUCENT_TRANSPARENCY).depthTest(ALWAYS_DEPTH_TEST).writeMaskState(COLOR_MASK).build(false));
        });
        int reqButtonPosX = i + 185;
        int reqButtonY = j + 5;

        requestButtonSelected = mouseX > reqButtonPosX && mouseX < reqButtonPosX + 12 && mouseY > reqButtonY && mouseY < reqButtonY + 12;

        if (requestButtonSelected) {
            context.drawTexture(GUI_TEXTURED, PLAY_SELECTED_TEXTURE, reqButtonPosX, reqButtonY, 32F, 32F, 12, 12, 8, 8, 8, 8);
        } else {
            context.drawTexture(GUI_TEXTURED, PLAY_TEXTURE, reqButtonPosX, reqButtonY, 32F, 32F, 12, 12, 8, 8, 8, 8);
        }
    }

    private void renderTrashButton(DrawContext context, int mouseX, int mouseY, int i, int j) {
        int trashButtonPosX = i + 200;
        int trashButtonY = j + 5;

        trashButtonSelected = mouseX > trashButtonPosX && mouseX < trashButtonPosX + 12 && mouseY > trashButtonY && mouseY < trashButtonY + 12;

        if (trashButtonSelected) {
            context.drawTexture(GUI_TEXTURED,TRASH_SELECTED_TEXTURE, trashButtonPosX, trashButtonY, 32F, 32F, 12, 12,8, 8, 8, 8);
        } else {
            context.drawTexture(GUI_TEXTURED,TRASH_TEXTURE, trashButtonPosX, trashButtonY, 32F, 32F, 12, 12, 8, 8, 8, 8);
        }
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
