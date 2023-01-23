package com.cinemamod.fabric.cef;

import net.minecraft.client.MinecraftClient;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefBrowserOsr;
import org.cef.browser.CefRequestContext;
import org.lwjgl.glfw.GLFW;

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

    public static int remapKeycode(int kc, char c) {
        switch (kc) {
            case GLFW.GLFW_KEY_BACKSPACE:
                return 0x08;
            case GLFW.GLFW_KEY_DELETE:
                return 0x2E;
            case GLFW.GLFW_KEY_DOWN:
                return 0x28;
            case GLFW.GLFW_KEY_ENTER:
                return 0x0D;
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
            default:
                return kc;
        }
    }

    public void sendKeyPress(int keyCode, int modifiers) {
        KeyEvent keyEvent = new KeyEvent(dummyComponent,
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                modifiers,
                keyCode,
                KeyEvent.CHAR_UNDEFINED);
        sendKeyEvent(keyEvent);
    }

    public void sendKeyRelease(int keyCode, int modifiers) {
        KeyEvent keyEvent = new KeyEvent(dummyComponent,
                KeyEvent.KEY_RELEASED,
                System.currentTimeMillis(),
                modifiers,
                keyCode,
                KeyEvent.CHAR_UNDEFINED);
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
                button);
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
