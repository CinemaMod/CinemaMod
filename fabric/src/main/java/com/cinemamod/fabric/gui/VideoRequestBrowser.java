package com.cinemamod.fabric.gui;

import com.cinemamod.fabric.cef.CefBrowserCinema;
import com.cinemamod.fabric.cef.CefUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget.Builder;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import java.awt.event.KeyEvent;

import org.lwjgl.glfw.GLFW;

public class VideoRequestBrowser extends Screen {

    protected static KeyBinding keyBinding;
    private static CefBrowserCinema browser;
    private static final int browserDrawOffset = 40;

    private ButtonWidget backBtn, fwdBtn, requestBtn, closeBtn;
    private TextFieldWidget urlField;

    protected VideoRequestBrowser() {
        super(Text.translatable("gui.cinemamod.videorequesttitle"));
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
        ButtonWidget.Builder requestBtnBuilder = new Builder(Text.translatable("gui.cinemamod.videorequestbtn"), button -> {
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
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!urlField.isFocused()) {
            urlField.setText(browser.getURL());
            urlField.setCursor(0, false); // If the URL is longer than the URL field, we want it to start at the beginning
        }
        super.render(context, mouseX, mouseY, delta);
        urlField.render(context, mouseX, mouseY, delta); // The URL bar looks better under everything else
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        int glId = browser.renderer.getTextureID();
        RenderSystem.setShaderTexture(0, glId);
        Tessellator t = Tessellator.getInstance();
        BufferBuilder builder = t.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        builder.vertex(browserDrawOffset, height - browserDrawOffset, 0).color(255, 255, 255, 255).texture(0.0f, 1.0f);
        builder.vertex(width - browserDrawOffset, height - browserDrawOffset, 0).color(255, 255, 255, 255).texture(1.0f, 1.0f);
        builder.vertex(width - browserDrawOffset, browserDrawOffset, 0).color(255, 255, 255, 255).texture(1.0f, 0.0f);
        builder.vertex(browserDrawOffset, browserDrawOffset, 0).color(255, 255, 255, 255).texture(0.0f, 0.0f);
        BufferRenderer.drawWithGlobalProgram(builder.end());
        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.enableDepthTest();
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
                return KeyEvent.VK_BACK_SPACE;
            case GLFW.GLFW_KEY_DELETE:
                return KeyEvent.VK_DELETE;
            case GLFW.GLFW_KEY_DOWN:
                return KeyEvent.VK_DOWN;
            case GLFW.GLFW_KEY_ENTER:
                return KeyEvent.VK_ENTER;
            case GLFW.GLFW_KEY_ESCAPE:
                return KeyEvent.VK_ESCAPE;
            case GLFW.GLFW_KEY_LEFT:
                return KeyEvent.VK_LEFT;
            case GLFW.GLFW_KEY_RIGHT:
                return KeyEvent.VK_RIGHT;
            case GLFW.GLFW_KEY_TAB:
                return KeyEvent.VK_TAB;
            case GLFW.GLFW_KEY_UP:
                return KeyEvent.VK_UP;
            case GLFW.GLFW_KEY_PAGE_UP:
                return KeyEvent.VK_PAGE_UP;
            case GLFW.GLFW_KEY_PAGE_DOWN:
                return KeyEvent.VK_PAGE_DOWN;
            case GLFW.GLFW_KEY_END:
                return KeyEvent.VK_END;
            case GLFW.GLFW_KEY_HOME:
                return KeyEvent.VK_HOME;
        }
        return -1;
    }

    private static int remapScanCode(int scanCode) {
        switch (scanCode) {
            case 327: return 0x47; // HOME
            case 328: return 0x48; // UP
            case 329: return 0x49; // PGUP
            case 331: return 0x4B; // LEFT
            case 333: return 0x4D; // RIGHT
            case 335: return 0x4F; // END
            case 336: return 0x50; // DOWN
            case 337: return 0x51; // PGDOWN
            case 338: return 0x52; // PGDOWN
            case 339: return 0x53; // DEL
        }
        return scanCode;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        if (urlField.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                String newURL = urlField.getText();
                urlField.setFocused(false);
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
            browser.sendKeyPress(remap, modifiers, remapScanCode(scanCode));
        }

        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        if (urlField.isFocused()) {
            return urlField.keyReleased(keyCode, scanCode, modifiers);
        }

        if (browser == null) {
            return true;
        }

        int remap = remapKeyCode(keyCode);
        if (remap != -1) {
            if (remap == KeyEvent.VK_ENTER) {
                browser.sendKeyTyped((char)13, 0);
            }
            browser.sendKeyRelease(remap, modifiers, remapScanCode(scanCode));
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
            urlField.setFocused(true);
            urlField.setEditable(true);
        }

        if (urlField.isFocused()) {
            return urlField.mouseClicked(mouseX, mouseY, button);
        }

        if (browser == null) {
            return true;
        }

        // Extra mouse buttons will cause a crash
        // by HeadlessToolkit.areExtraMouseButtonsEnabled
        // Also, right click is middle click?
        if (button > 0) return true;

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

        // Extra mouse buttons will cause a crash
        // by HeadlessToolkit.areExtraMouseButtonsEnabled
        // Also, right click is middle click?
        if (button > 0) return true;

        browser.sendMouseRelease(mx(mouseX), my(mouseY), button);

        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount, double verticalAmount) {
        super.mouseScrolled(mouseX, mouseY, amount, verticalAmount);

        if (urlField.isFocused()) {
            urlField.mouseScrolled(mouseX, mouseY, amount, verticalAmount);
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
