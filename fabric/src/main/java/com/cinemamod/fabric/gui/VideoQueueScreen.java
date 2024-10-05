package com.cinemamod.fabric.gui;

import com.cinemamod.fabric.CinemaModClient;
import com.cinemamod.fabric.gui.widget.VideoQueueWidget;
import com.cinemamod.fabric.util.NetworkUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget.Builder;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class VideoQueueScreen extends Screen {

    protected static final Identifier TEXTURE = Identifier.of("textures/gui/social_interactions.png");
    protected static KeyBinding keyBinding;

    public VideoQueueWidget videoQueueWidget;

    public VideoQueueScreen() {
        super(Text.of("Video Queue"));
    }

    @Override
    protected void init() {
        videoQueueWidget = new VideoQueueWidget(this, client, this.width, this.height, 68, this.method_31361(), 19);
        ButtonWidget.Builder videoSettingsBuilder = new Builder(Text.of("Video Settings"), button -> {
            client.setScreen(new VideoSettingsScreen());
        });

        videoSettingsBuilder.dimensions(method_31362() + 23, method_31359() + 78, 196, 20);

        addDrawableChild(videoSettingsBuilder.build());
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

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        int i = this.method_31362() + 3;
        super.renderBackground(context, mouseX, mouseY, delta);
        context.drawTexture(TEXTURE, i, 64, 1, 1, 236, 8);
        int j = this.method_31360();
        for (int k = 0; k < j; ++k)
            context.drawTexture(TEXTURE, i, 72 + 16 * k, 1, 10, 236, 16);
        context.drawTexture(TEXTURE, i, 72 + 16 * j, 1, 27, 236, 8);
        context.drawCenteredTextWithShadow(this.client.textRenderer, Text.of("Video Queue - " + videoQueueWidget.children().size() + " entries"), this.width / 2, 64 - 10, -1);
        if (videoQueueWidget.children().isEmpty()) {
            context.drawCenteredTextWithShadow(this.client.textRenderer, Text.of("No videos queued"), this.width / 2, (56 + this.method_31361()) / 2, -1);
        } else {
            if (videoQueueWidget.getScrollAmount() == 0f) {
                context.drawCenteredTextWithShadow(this.client.textRenderer, Text.of("UP NEXT ->"), -158 + this.width / 2, 64 + 12, -1);
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        videoQueueWidget.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
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
        if (keyBinding.matchesKey(keyCode, scanCode)) {
            close();
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount, double verticalAmount) {
        videoQueueWidget.mouseScrolled(mouseX, mouseY, amount, verticalAmount);
        return super.mouseScrolled(mouseX, mouseY, amount, verticalAmount);
    }

    public static void registerKeyInput() {
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cinemamod.openvideoqueue",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.cinemamod.keybinds"
        ));

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!CinemaModClient.getInstance().getScreenManager().hasActiveScreen()) return;

            if (keyBinding.wasPressed()) {
                client.setScreen(new VideoQueueScreen());
            }
        });
    }

}
