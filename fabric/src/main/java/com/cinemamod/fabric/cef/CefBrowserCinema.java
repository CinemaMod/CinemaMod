package com.cinemamod.fabric.cef;

import net.minecraft.client.MinecraftClient;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefBrowserOsr;
import org.cef.browser.CefRequestContext;
import org.cef.event.CefKeyEvent;
import org.cef.event.CefMouseEvent;
import org.cef.event.CefMouseWheelEvent;

import java.awt.*;
import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class CefBrowserCinema extends CefBrowserOsr {

    public final CefBrowserCinemaRenderer renderer = new CefBrowserCinemaRenderer(true);

    public CefBrowserCinema(CefClient client, String url, boolean transparent, CefRequestContext context) {
        super(client, url, transparent, context);
        MinecraftClient.getInstance().submit(renderer::initialize);
    }

    @Override
    public void onPaint(CefBrowser browser, boolean popup, Rectangle[] dirtyRects, ByteBuffer buffer, int width, int height) {
        renderer.onPaint(buffer, width, height);
    }

    public void sendKeyPress(int keyCode, int modifiers, long scanCode) {
        CefKeyEvent e = new CefKeyEvent(keyCode, CefKeyEvent.KEY_PRESS, modifiers, (char) keyCode);
        sendKeyEvent(e);
    }

    public void sendKeyRelease(int keyCode, int modifiers, long scanCode) {
        CefKeyEvent e = new CefKeyEvent(keyCode, CefKeyEvent.KEY_RELEASE, modifiers, (char) keyCode);
        sendKeyEvent(e);
    }

    public void sendKeyTyped(char c, int modifiers) {
        CefKeyEvent e = new CefKeyEvent(c, CefKeyEvent.KEY_TYPE, modifiers, c);
        sendKeyEvent(e);
    }

    public void sendMouseMove(int mouseX, int mouseY) {
        CefMouseEvent e = new CefMouseEvent(503, mouseX, mouseY, CefMouseEvent.BUTTON1_MASK, 0, 0);
        sendMouseEvent(e);
    }

    public void sendMousePress(int mouseX, int mouseY, int button) {
        CefMouseEvent e = new CefMouseEvent(GLFW_PRESS, mouseX, mouseY, CefMouseEvent.BUTTON1_MASK, 1, button);
        sendMouseEvent(e);
    }

    public void sendMouseRelease(int mouseX, int mouseY, int button) {
        CefMouseEvent e = new CefMouseEvent(GLFW_RELEASE, mouseX, mouseY, CefMouseEvent.BUTTON1_MASK, 1, button);
        sendMouseEvent(e);
    }

    public void sendMouseWheel(int mouseX, int mouseY, int mods, int amount, int rotation) {
        CefMouseWheelEvent e = new CefMouseWheelEvent(CefMouseWheelEvent.WHEEL_UNIT_SCROLL, amount, mouseX, mouseY, mods);
        sendMouseWheelEvent(e);
    }

    public void resize(int width, int height) {
        browser_rect_.setBounds(0, 0, width, height);
        wasResized(width, height);
    }

    public void close() {
        renderer.cleanup();
        super.close(true);
    }

}
