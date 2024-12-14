package com.cinemamod.fabric.gui;

import com.cinemamod.fabric.CinemaMod;
import com.cinemamod.fabric.CinemaModClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.ButtonWidget.Builder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class VideoSettingsScreen extends Screen {

    protected static final Identifier TEXTURE = Identifier.of(CinemaMod.MODID, "textures/gui/menuui_trans.png");
    private boolean shouldReloadScreen;

    public VideoSettingsScreen() {
        super(Text.translatable("gui.cinemamod.videosettingstitle"));
    }

    private static CheckboxWidget checkboxWidget(int x, int y, int width, int height, Text text, boolean checked, CheckboxWidget.Callback callback) {
        CheckboxWidget widget = CheckboxWidget.builder(text, MinecraftClient.getInstance().textRenderer)
                .pos(x, y)
                .checked(checked)
                .callback(callback)
                .build();
        widget.setWidth(width);
        widget.setHeight(height);
        return widget;
    }

    @Override
    protected void init() {
        addDrawableChild(new SliderWidget(method_31362() + 23, 78, 196, 20, Text.translatable("gui.cinemamod.videosettingsvolume"),
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
        addDrawableChild(checkboxWidget(method_31362() + 23, 110, 196, 20, Text.translatable("gui.cinemamod.videosettingsmute"),
                CinemaModClient.getInstance().getVideoSettings().isMuteWhenAltTabbed(),
                (checkbox, checked) -> CinemaModClient.getInstance().getVideoSettings().setMuteWhenAltTabbed(checked)
        ));
        addDrawableChild(checkboxWidget(method_31362() + 23, 142, 196, 20, Text.translatable("gui.cinemamod.videosettingscrosshair"),
                CinemaModClient.getInstance().getVideoSettings().isHideCrosshair(),
                (checkbox, checked) -> CinemaModClient.getInstance().getVideoSettings().setHideCrosshair(checked)
        ));
        ButtonWidget.Builder screenResolutionBuilder = new Builder(
            Text.translatable("gui.cinemamod.videosettingsresolution", CinemaModClient.getInstance().getVideoSettings().getBrowserResolution(), "p"),
             button ->
        {
            CinemaModClient.getInstance().getVideoSettings().setNextBrowserResolution();
            button.setMessage(Text.translatable("gui.cinemamod.videosettingsresolution", CinemaModClient.getInstance().getVideoSettings().getBrowserResolution(), "p"));
            shouldReloadScreen = true;
        });
        screenResolutionBuilder.dimensions(method_31362() + 23, 142 + 32, 196, 20);
        addDrawableChild(screenResolutionBuilder.build());
        ButtonWidget.Builder browserRefreshRateBuilder = new Builder(
                Text.translatable("gui.cinemamod.videosettingsrefreshrate", CinemaModClient.getInstance().getVideoSettings().getBrowserRefreshRate(), "fps"),
                button ->
                {
                    CinemaModClient.getInstance().getVideoSettings().setNextBrowserRefreshRate();
                    button.setMessage(Text.translatable("gui.cinemamod.videosettingsrefreshrate", CinemaModClient.getInstance().getVideoSettings().getBrowserRefreshRate(), "fps"));
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

    private static final Function<Identifier, RenderLayer> GUI_TEXTURED = null;

    public void renderBackground(DrawContext context) {
        int i = this.method_31362() + 3;
        context.drawTexture(GUI_TEXTURED,TEXTURE, i, 64, 1, 1, 236, 8, 8,8);
        int j = this.method_31360();
        for (int k = 0; k < j; ++k)
            context.drawTexture(GUI_TEXTURED,TEXTURE, i, 72 + 16 * k, 1, 10, 236, 16, 8,8);
        context.drawTexture(GUI_TEXTURED,TEXTURE, i, 72 + 16 * j, 1, 27, 236, 8, 8,8);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.client.textRenderer, Text.translatable("gui.cinemamod.videosettingstitle"), this.width / 2, 64 - 10, -1);
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
