package com.cinemamod.fabric.gui;

import com.cinemamod.fabric.CinemaModClient;
import com.cinemamod.fabric.gui.widget.VideoHistoryListWidget;
import com.cinemamod.fabric.gui.widget.VideoListWidget;
import com.cinemamod.fabric.video.list.VideoList;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Locale;

public class VideoHistoryScreen extends Screen {

    protected static final Identifier TEXTURE = new Identifier("textures/gui/social_interactions.png");
    protected static final Text SEARCH_TEXT = ((MutableText) Text.of("gui.socialInteractions.search_hint")).formatted(Formatting.ITALIC).formatted(Formatting.GRAY);

    private TextFieldWidget searchBox;
    private VideoListWidget videoListWidget;
    private String currentSearch = "";

    public VideoHistoryScreen() {
        super(Text.of("Video History"));
    }

    @Override
    protected void init() {
        //client.keyboard.setRepeatEvents(true);
        String string = searchBox != null ? searchBox.getText() : "";
        searchBox = new TextFieldWidget(textRenderer, method_31362() + 28, 78, 196, 16, SEARCH_TEXT);
        searchBox.setMaxLength(16);
        searchBox.setDrawsBackground(false);
        searchBox.setVisible(true);
        searchBox.setEditableColor(16777215);
        searchBox.setText(string);
        searchBox.setChangedListener(this::onSearchChange);
        addDrawableChild(searchBox);
        VideoList videoList = CinemaModClient.getInstance().getVideoListManager().getHistory();
        videoListWidget = new VideoHistoryListWidget(videoList, client, this.width, this.height, 88, this.method_31361(), 19);
    }

    @Override
    public void removed() {
        //client.keyboard.setRepeatEvents(false);
    }

    @Override
    public void tick() {
        super.tick();
        this.searchBox.tick();
    }

    public void renderBackground(MatrixStack matrices) {
        int i = this.method_31362() + 3;
        super.renderBackground(matrices);
        RenderSystem.setShaderTexture(0, TEXTURE);
        this.drawTexture(matrices, i, 64, 1, 1, 236, 8);
        int j = this.method_31360();
        for (int k = 0; k < j; ++k)
            this.drawTexture(matrices, i, 72 + 16 * k, 1, 10, 236, 16);
        this.drawTexture(matrices, i, 72 + 16 * j, 1, 27, 236, 8);
        this.drawTexture(matrices, i + 10, 76, 243, 1, 12, 12);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        videoListWidget.render(matrices, mouseX, mouseY, delta);
        if (!this.searchBox.isFocused() && this.searchBox.getText().isEmpty()) {
            drawTextWithShadow(matrices, this.client.textRenderer, SEARCH_TEXT, this.searchBox.getX(), this.searchBox.getY(), -1);
        } else {
            this.searchBox.render(matrices, mouseX, mouseY, delta);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    private int method_31359() {
        return Math.max(52, this.height - 128 - 16);
    }

    private int method_31360() {
        return this.method_31359() / 16;
    }

    private int method_31361() {
        return 80 + this.method_31360() * 16 - 8;
    }

    private int method_31362() {
        return (this.width - 238) / 2;
    }

    private void onSearchChange(String currentSearch) {
        currentSearch = currentSearch.toLowerCase(Locale.ROOT);
        if (!currentSearch.equals(this.currentSearch)) {
            videoListWidget.setSearch(currentSearch);
            this.currentSearch = currentSearch;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.searchBox.isFocused()) {
            this.searchBox.mouseClicked(mouseX, mouseY, button);
        }

        return super.mouseClicked(mouseX, mouseY, button) || videoListWidget.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.searchBox.isFocused() && this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            close();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        videoListWidget.mouseScrolled(mouseX, mouseY, amount);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

}
