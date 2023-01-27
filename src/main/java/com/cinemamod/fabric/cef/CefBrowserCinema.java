package com.cinemamod.fabric.cef;

import net.minecraft.client.MinecraftClient;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefBrowserOsr;
import org.cef.browser.CefRequestContext;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.nio.ByteBuffer;

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
        CefBrowserKeyEvent keyEvent = new CefBrowserKeyEvent(dummyComponent,
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                modifiers,
                keyCode,
                KeyEvent.CHAR_UNDEFINED,
                scanCode);
        sendKeyEvent(keyEvent);
    }

    public void sendKeyRelease(int keyCode, int modifiers, long scanCode) {
        CefBrowserKeyEvent keyEvent = new CefBrowserKeyEvent(dummyComponent,
                KeyEvent.KEY_RELEASED,
                System.currentTimeMillis(),
                modifiers,
                keyCode,
                KeyEvent.CHAR_UNDEFINED,
                scanCode);
        sendKeyEvent(keyEvent);
    }

    public void sendKeyTyped(char c, int modifiers) {
        KeyEvent keyEvent = new KeyEvent(dummyComponent,
                KeyEvent.KEY_TYPED,
                System.currentTimeMillis(),
                modifiers,
                KeyEvent.VK_UNDEFINED,
                c);
        sendKeyEvent(keyEvent);
    }

    public void sendMouseMove(int mouseX, int mouseY) {
        MouseEvent mouseEvent = new MouseEvent(dummyComponent,
                MouseEvent.MOUSE_MOVED,
                System.currentTimeMillis(),
                0,
                mouseX,
                mouseY,
                0,
                false);
        sendMouseEvent(mouseEvent);
    }

    public void sendMousePress(int mouseX, int mouseY, int button) {
        MouseEvent mouseEvent = new MouseEvent(dummyComponent,
                MouseEvent.MOUSE_PRESSED,
                System.currentTimeMillis(),
                0,
                mouseX,
                mouseY,
                1,
                false,
                button + 1);
        sendMouseEvent(mouseEvent);
    }

    public void sendMouseRelease(int mouseX, int mouseY, int button) {
        MouseEvent mouseEvent = new MouseEvent(dummyComponent,
                MouseEvent.MOUSE_RELEASED,
                System.currentTimeMillis(),
                0,
                mouseX,
                mouseY,
                1,
                false,
                button + 1);
        sendMouseEvent(mouseEvent);

        mouseEvent = new MouseEvent(dummyComponent,
                MouseEvent.MOUSE_CLICKED,
                System.currentTimeMillis(),
                0,
                mouseX,
                mouseY,
                1,
                false,
                button + 1);
        sendMouseEvent(mouseEvent);
    }

    public void sendMouseWheel(int mouseX, int mouseY, int mods, int amount, int rotation) {
        MouseWheelEvent mouseWheelEvent = new MouseWheelEvent(dummyComponent,
                MouseEvent.MOUSE_WHEEL,
                System.currentTimeMillis(),
                mods,
                mouseX,
                mouseY,
                0,
                false,
                MouseWheelEvent.WHEEL_UNIT_SCROLL,
                amount,
                rotation);
        sendMouseWheelEvent(mouseWheelEvent);
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
