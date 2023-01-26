package com.cinemamod.fabric.gui;

import com.cinemamod.fabric.cef.CefBrowserCinema;
import com.cinemamod.fabric.cef.CefUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget.Builder;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import static java.awt.event.KeyEvent.VK_ENTER;

public class VideoRequestBrowser extends Screen {

    protected static KeyBinding keyBinding;
    private static CefBrowserCinema browser;
    private static final int browserDrawOffset = 40;

    private ButtonWidget backBtn, fwdBtn, requestBtn, closeBtn;
    private TextFieldWidget urlField;

    protected VideoRequestBrowser() {
        super(Text.of("Video Request Browser"));
    }

    @Override
    protected void init() {
        super.init();

        if (CefUtil.isInit() && browser == null) {
            browser = CefUtil.createBrowser("https://google.com", vx(width), vy(height));
        }

        if (browser == null) return;

        ButtonWidget.Builder backBtnBuilder = new Builder(Text.of("<"), button -> {
            browser.goBack();
        });
        ButtonWidget.Builder fwdBtnBuilder = new Builder(Text.of(">"), button -> {
            browser.goForward();
        });
        ButtonWidget.Builder requestBtnBuilder = new Builder(Text.of("Request"), button -> {
            System.out.println("TODO, request button");
        });
        ButtonWidget.Builder closeBtnBuilder = new Builder(Text.of("X"), button -> {
            close();
        });

        backBtnBuilder.dimensions(browserDrawOffset, browserDrawOffset - 20, 20, 20);
        fwdBtnBuilder.dimensions(browserDrawOffset + 20, browserDrawOffset - 20, 20, 20);
        requestBtnBuilder.dimensions(width - browserDrawOffset - 20 - 60, browserDrawOffset - 20, 60, 20);
        closeBtnBuilder.dimensions(width - browserDrawOffset - 20, browserDrawOffset - 20, 20, 20);

        addDrawableChild(backBtnBuilder.build());
        addDrawableChild(fwdBtnBuilder.build());
        addDrawableChild(requestBtnBuilder.build());
        addDrawableChild(closeBtnBuilder.build());

        urlField = new TextFieldWidget(client.textRenderer, browserDrawOffset + 40, browserDrawOffset - 20 + 1, width - browserDrawOffset - 160, 20, Text.of(""));
        urlField.setMaxLength(65535);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!urlField.isFocused()) {
            urlField.setText(browser.getURL());
            urlField.setCursor(0); // If the URL is longer than the URL field, we want it to start at the beginning
        }
        urlField.render(matrices, mouseX, mouseY, delta); // The URL bar looks better under everything else
        super.render(matrices, mouseX, mouseY, delta);
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
        int glId = browser.renderer.getTextureID();
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

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);

        if (browser == null) {
            return;
        }

        if (width > 100 && height > 100) {
            browser.resize(vx(width), vy(height));
        }
    }

    private static int remapKeyCode(int keyCode) {
        switch (keyCode) {
            case GLFW.GLFW_KEY_BACKSPACE:
                return 0x08;
            case GLFW.GLFW_KEY_DELETE:
                return 0x2E;
            case GLFW.GLFW_KEY_DOWN:
                return 0x28;
            case GLFW.GLFW_KEY_ENTER:
                return VK_ENTER;
            case GLFW.GLFW_KEY_ESCAPE:
                return 0x1B;
            case GLFW.GLFW_KEY_LEFT:
                return 0x25;
            case GLFW.GLFW_KEY_RIGHT:
                return 0x27;
            case GLFW.GLFW_KEY_TAB:
                return 0x09;
            case GLFW.GLFW_KEY_UP:
                return 0x26;
            case GLFW.GLFW_KEY_PAGE_UP:
                return 0x21;
            case GLFW.GLFW_KEY_PAGE_DOWN:
                return 0x22;
            case GLFW.GLFW_KEY_END:
                return 0x23;
            case GLFW.GLFW_KEY_HOME:
                return 0x24;
        }
        return -1;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);

        if (urlField.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                String newURL = urlField.getText();
                urlField.setTextFieldFocused(false);
                browser.loadURL(newURL);
                return true;
            }

            return urlField.keyPressed(keyCode, scanCode, modifiers);
        }

        if (browser == null) {
            return true;
        }

        int remap = remapKeyCode(keyCode);
        if (remap != -1) {
            browser.sendKeyPress(remap, modifiers);
        }

        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        super.keyReleased(keyCode, scanCode, modifiers);

        if (urlField.isFocused()) {
            return urlField.keyReleased(keyCode, scanCode, modifiers);
        }

        if (browser == null) {
            return true;
        }

        int remap = remapKeyCode(keyCode);
        if (remap != -1) {
            if (remap == VK_ENTER) {
                browser.sendKeyTyped((char)13, 0);
            }
            browser.sendKeyRelease(remap, modifiers);
        }

        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        super.charTyped(chr, modifiers);

        if (urlField.isFocused()) {
            return urlField.charTyped(chr, modifiers);
        }

        if (browser == null) {
            return true;
        }

        browser.sendKeyTyped(chr, modifiers);

        return true;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);

        if (urlField.isFocused()) {
            urlField.mouseMoved(mouseX, mouseY);
            return;
        }

        if (browser == null) {
            return;
        }

        browser.sendMouseMove(mx(mouseX), my(mouseY));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        if (urlField.isMouseOver(mouseX, mouseY)) {
            urlField.setTextFieldFocused(true);
            urlField.setEditable(true);
        }

        if (urlField.isFocused()) {
            return urlField.mouseClicked(mouseX, mouseY, button);
        }

        if (browser == null) {
            return true;
        }

        browser.sendMousePress(mx(mouseX), my(mouseY), button);

        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);

        if (urlField.isFocused()) {
            urlField.mouseReleased(mouseX, mouseY, button);
        }

        if (browser == null) {
            return true;
        }

        browser.sendMouseRelease(mx(mouseX), my(mouseY), button);

        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        super.mouseScrolled(mouseX, mouseY, amount);

        if (urlField.isFocused()) {
            urlField.mouseScrolled(mouseX, mouseY, amount);
        }

        if (browser == null) {
            return true;
        }

        browser.sendMouseWheel(mx(mouseX), my(mouseY), 0, (int)amount, 90);

        return true;
    }

    public int vx(double x) {
        return (int) ((x - browserDrawOffset * 2) * client.getWindow().getScaleFactor());
    }

    public int vy(double y) {
        return (int) ((y - browserDrawOffset * 2) * client.getWindow().getScaleFactor());
    }

    public int mx(double x) {
        return (int) ((x - browserDrawOffset) * client.getWindow().getScaleFactor());
    }

    public int my(double y) {
        return (int) ((y - browserDrawOffset) * client.getWindow().getScaleFactor());
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

}
