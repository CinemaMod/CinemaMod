package com.cinemamod.fabric.cef;

import net.minecraft.client.MinecraftClient;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefBrowserOsr;
import org.cef.browser.CefRequestContext;

import java.awt.*;
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

    public void resize(int width, int height) {
        browser_rect_.setBounds(0, 0, width, height);
        wasResized(width, height);
    }

    public void close() {
        renderer.cleanup();
        super.close(true);
    }

}
