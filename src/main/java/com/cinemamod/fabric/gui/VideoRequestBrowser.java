package com.cinemamod.fabric.gui;

import com.cinemamod.fabric.cef.CefUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.cef.browser.CefBrowserOsr;
import org.lwjgl.glfw.GLFW;

public class VideoRequestBrowser extends Screen {

    protected static KeyBinding keyBinding;
    private static CefBrowserOsr browser;

    private int width;
    private int height;

    protected VideoRequestBrowser(int width, int height) {
        super(Text.of("Video Request Browser"));

        this.width = width;
        this.height = height;

        if (CefUtil.isInit() && browser == null) {
            browser = CefUtil.createBrowser("https://google.com", width, height);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        int glId = browser.renderer_.texture_id_[0];
        RenderSystem.setShaderTexture(0, glId);
        Tessellator t = Tessellator.getInstance();
        BufferBuilder buffer = t.getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        buffer.vertex(0, height, 0).color(255, 255, 255, 255).texture(0.0f, 1.0f).next();
        buffer.vertex(width, height, 0).color(255, 255, 255, 255).texture(1.0f, 1.0f).next();
        buffer.vertex(width, 0, 0).color(255, 255, 255, 255).texture(1.0f, 0.0f).next();
        buffer.vertex(0, 0, 0).color(255, 255, 255, 255).texture(0.0f, 0.0f).next();
        t.draw();
        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.enableDepthTest();
        matrices.pop();
    }

    @Override
    public void close() {
        super.close();
        browser.close();
        browser = null;
    }

    public static void registerKeyInput() {
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cinemamod.openrequestbrowser",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.cinemamod.keybinds"
        ));

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (keyBinding.wasPressed()) {
                // TODO adjust width and height based on size of MC window
                client.setScreen(new VideoRequestBrowser(200, 200));
            }
        });
    }

    public static void registerCefTick() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (browser != null) {
                browser.update();
            }
        });
    }

}
