package com.cinemamod.fabric.gui;

import com.cinemamod.fabric.cef.CefUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ButtonWidget.Builder;
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
    private static final int browserDrawOffset = 40;

    @SuppressWarnings("unused")
    private ButtonWidget backBtn, fwdBtn, requestBtn, closeBtn;
    private TextFieldWidget urlField;

    protected VideoRequestBrowser() {
        super(Text.of("Video Request Browser"));
    }

    @Override
    protected void init() {
        super.init();

        if (CefUtil.isInit() && browser == null) {
            browser = CefUtil.createBrowser("https://google.com", width, height);
        }

        if (browser == null) return;

        browser.resize(client.getWindow().getWidth(), client.getWindow().getHeight() - scaleY(20));

        ButtonWidget.Builder backBtnBuilder = new Builder(Text.of("<"), button -> {
            System.out.println("back button");
        });
        ButtonWidget.Builder fwdBtnBuilder = new Builder(Text.of(">"), button -> {
            System.out.println("fwd button");
        });
        ButtonWidget.Builder requestBtnBuilder = new Builder(Text.of("Request"), button -> {
            System.out.println("request button");
        });
        ButtonWidget.Builder closeBtnBuilder = new Builder(Text.of("X"), button -> {
            System.out.println("close button");
        });

        backBtnBuilder.dimensions(browserDrawOffset, browserDrawOffset - 20, 20, 20);
        fwdBtnBuilder.dimensions(browserDrawOffset + 20, browserDrawOffset - 20, 20, 20);
        requestBtnBuilder.dimensions(width - browserDrawOffset + 20, browserDrawOffset - 20, 20, 20);
        closeBtnBuilder.dimensions(width - browserDrawOffset, browserDrawOffset - 20, 20, 20);

        addDrawableChild(backBtnBuilder.build());
        addDrawableChild(fwdBtnBuilder.build());
        addDrawableChild(requestBtnBuilder.build());
        addDrawableChild(closeBtnBuilder.build());

        urlField = new TextFieldWidget(client.textRenderer, browserDrawOffset + 40, browserDrawOffset - 20 + 1, width - browserDrawOffset - 160, 20, Text.of(""));
        urlField.setMaxLength(65535);
        urlField.setText(browser.getURL()); // why does getURL return an empty string here?
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        urlField.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
        int glId = browser.renderer_.texture_id_[0];
        RenderSystem.setShaderTexture(0, glId);
        Tessellator t = Tessellator.getInstance();
        BufferBuilder buffer = t.getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        buffer.vertex(browserDrawOffset, height - browserDrawOffset, 0).color(255, 255, 255, 255).texture(0.0f, 1.0f).next();
        buffer.vertex(width - browserDrawOffset, height - browserDrawOffset, 0).color(255, 255, 255, 255).texture(1.0f, 1.0f).next();
        buffer.vertex(width - browserDrawOffset, browserDrawOffset, 0).color(255, 255, 255, 255).texture(1.0f, 0.0f).next();
        buffer.vertex(browserDrawOffset, browserDrawOffset, 0).color(255, 255, 255, 255).texture(0.0f, 0.0f).next();
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

    public int scaleY(int y) {
        assert client != null;
        double sy = ((double) y) / ((double) height) * ((double) client.getWindow().getHeight());
        return (int) sy;
    }

    public int scaleX(int x) {
        assert client != null;
        double sx = ((double) x) / ((double) width) * ((double) client.getWindow().getWidth());
        return (int) sx;
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
                client.setScreen(new VideoRequestBrowser());
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
