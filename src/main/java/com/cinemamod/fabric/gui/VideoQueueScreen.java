package com.cinemamod.fabric.gui;

import com.cinemamod.fabric.CinemaModClient;
import com.cinemamod.fabric.gui.widget.VideoQueueWidget;
import com.cinemamod.fabric.util.NetworkUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class VideoQueueScreen extends Screen {

    protected static final Identifier TEXTURE = new Identifier("textures/gui/social_interactions.png");

    public VideoQueueWidget videoQueueWidget;

    public VideoQueueScreen() {
        super(Text.of("Video Queue"));
    }

    @Override
    protected void init() {
        videoQueueWidget = new VideoQueueWidget(this, client, this.width, this.height, 68, this.method_31361(), 19);
        addDrawableChild(new ButtonWidget(method_31362() + 23, method_31359() + 78, 196, 20, Text.of("Video Settings"), button -> {
            client.setScreen(new VideoSettingsScreen());
        }));
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

    public void renderBackground(MatrixStack matrices) {
        int i = this.method_31362() + 3;
        super.renderBackground(matrices);
        RenderSystem.setShaderTexture(0, TEXTURE);
        this.drawTexture(matrices, i, 64, 1, 1, 236, 8);
        int j = this.method_31360();
        for (int k = 0; k < j; ++k)
            this.drawTexture(matrices, i, 72 + 16 * k, 1, 10, 236, 16);
        this.drawTexture(matrices, i, 72 + 16 * j, 1, 27, 236, 8);
        drawCenteredText(matrices, this.client.textRenderer, Text.of("Video Queue - " + videoQueueWidget.children().size() + " entries"), this.width / 2, 64 - 10, -1);
        if (videoQueueWidget.children().isEmpty()) {
            drawCenteredText(matrices, this.client.textRenderer, Text.of("No videos queued"), this.width / 2, (56 + this.method_31361()) / 2, -1);
        } else {
            if (videoQueueWidget.getScrollAmount() == 0f) {
                drawCenteredText(matrices, this.client.textRenderer, Text.of("UP NEXT ->"), -158 + this.width / 2, 64 + 12, -1);
            }
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        videoQueueWidget.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        super.close();
        NetworkUtil.sendShowVideoTimelinePacket();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button) || videoQueueWidget.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (client.options.dropKey.matchesKey(keyCode, scanCode)) {
            close();
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        videoQueueWidget.mouseScrolled(mouseX, mouseY, amount);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    public static void registerKeyInput() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!CinemaModClient.getInstance().getScreenManager().hasActiveScreen()) return;

            if (client.options.dropKey.wasPressed()) {
                client.setScreen(new VideoQueueScreen());
            }
        });
    }

}
