package com.cinemamod.fabric.gui;

import com.cinemamod.fabric.CinemaModClient;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.ButtonWidget.Builder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class VideoSettingsScreen extends Screen {

    protected static final Identifier TEXTURE = new Identifier("textures/gui/social_interactions.png");
    private boolean shouldReloadScreen;

    public VideoSettingsScreen() {
        super(Text.of("Video Settings"));
    }

    @Override
    protected void init() {
        addDrawableChild(new SliderWidget(method_31362() + 23, 78, 196, 20, Text.of("Volume"),
                CinemaModClient.getInstance().getVideoSettings().getVolume()) {
            @Override
            protected void updateMessage() {
            }

            @Override
            protected void applyValue() {
                for (com.cinemamod.fabric.screen.Screen screen : CinemaModClient.getInstance().getScreenManager().getScreens())
                    screen.setVideoVolume((float) value);
                CinemaModClient.getInstance().getVideoSettings().setVolume((float) value);
            }
        });
        addDrawableChild(new CheckboxWidget(method_31362() + 23, 110, 196, 20, Text.of("Mute video while alt-tabbed"),
                CinemaModClient.getInstance().getVideoSettings().isMuteWhenAltTabbed()) {
            @Override
            public void onPress() {
                super.onPress();
                CinemaModClient.getInstance().getVideoSettings().setMuteWhenAltTabbed(isChecked());
            }
        });
        addDrawableChild(new CheckboxWidget(method_31362() + 23, 142, 196, 20, Text.of("Hide crosshair while video playing"),
                CinemaModClient.getInstance().getVideoSettings().isHideCrosshair()) {
            @Override
            public void onPress() {
                super.onPress();
                CinemaModClient.getInstance().getVideoSettings().setHideCrosshair(isChecked());
            }
        });
        ButtonWidget.Builder screenResolutionBuilder = new Builder(
            Text.of("Screen resolution: " + CinemaModClient.getInstance().getVideoSettings().getBrowserResolution() + "p"),
             button ->
        {
            CinemaModClient.getInstance().getVideoSettings().setNextBrowserResolution();
            button.setMessage(Text.of("Screen resolution: " + CinemaModClient.getInstance().getVideoSettings().getBrowserResolution() + "p"));
            shouldReloadScreen = true;
        });
        screenResolutionBuilder.dimensions(method_31362() + 23, 142 + 32, 196, 20);
        addDrawableChild(screenResolutionBuilder.build());
        ButtonWidget.Builder browserRefreshRateBuilder = new Builder(
                Text.of("Screen refresh rate: " + CinemaModClient.getInstance().getVideoSettings().getBrowserRefreshRate() + " fps"),
                button ->
                {
                    CinemaModClient.getInstance().getVideoSettings().setNextBrowserRefreshRate();
                    button.setMessage(Text.of("Screen refresh rate: " + CinemaModClient.getInstance().getVideoSettings().getBrowserRefreshRate() + " fps"));
                    shouldReloadScreen = true;
                });
        browserRefreshRateBuilder.dimensions(method_31362() + 23, 142 + 32 + 32, 196, 20);
        addDrawableChild(browserRefreshRateBuilder.build());
    }

    private int method_31359() {
        return Math.max(52, this.height - 128 - 16);
    }

    private int method_31360() {
        return this.method_31359() / 16;
    }

    private int method_31362() {
        return (this.width - 238) / 2;
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
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.client.textRenderer, Text.of("Video Settings"), this.width / 2, 64 - 10, -1);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        super.close();
        CinemaModClient.getInstance().getVideoSettings().saveAsync();
        if (shouldReloadScreen) {
            for (com.cinemamod.fabric.screen.Screen screen : CinemaModClient.getInstance().getScreenManager().getScreens()) {
                if (screen.hasBrowser()) {
                    screen.reload();
                }
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            close();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

}
