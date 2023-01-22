package com.cinemamod.fabric.cef;

import net.minecraft.client.MinecraftClient;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefBrowserOsr;
import org.cef.browser.CefRequestContext;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.nio.ByteBuffer;

public class CefBrowserCinema extends CefBrowserOsr {

    public final CefBrowserCinemaRenderer renderer = new CefBrowserCinemaRenderer(true);
    private final CefImageData imageData = new CefImageData();

    private class CefBrowserCinemaUpdateThread extends Thread {
        @Override
        public void run() {
            while (true) {
                synchronized (imageData) {
                    if (imageData.shouldClose) {
                        return;
                    }

                    if (imageData.hasFrame) {
                        MinecraftClient.getInstance().submit(() -> renderer.onPaint(imageData.dirtyRects, imageData.buffer, imageData.width, imageData.height, imageData.fullReRender));
                        imageData.hasFrame = false;
                        imageData.fullReRender = false;
                    }
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public CefBrowserCinema(CefClient client, String url, boolean transparent, CefRequestContext context) {
        super(client, url, transparent, context);
        new CefBrowserCinemaUpdateThread().start();
    }

    @Override
    public void onPaint(CefBrowser browser, boolean popup, Rectangle[] dirtyRects, ByteBuffer buffer, int width, int height) {
        final int size = (width * height) << 2;

        synchronized (imageData) {
            if (imageData.hasFrame) // The previous frame was not uploaded to GL texture, so we skip it and render this on instead
                imageData.fullReRender = true;

            if (imageData.buffer == null || size != imageData.buffer.capacity()) // This only happens when the browser gets resized
                imageData.buffer = BufferUtils.createByteBuffer(size);

            imageData.buffer.position(0);
            imageData.buffer.limit(buffer.limit());
            buffer.position(0);
            imageData.buffer.put(buffer);
            imageData.buffer.position(0);

            imageData.width = width;
            imageData.height = height;
            imageData.dirtyRects = dirtyRects;
            imageData.hasFrame = true;
        }
    }

    public void resize(int width, int height) {
        browser_rect_.setBounds(0, 0, width, height);
        wasResized(width, height);
    }

    public void close() {
        renderer.cleanup();
        super.close(true);
        imageData.shouldClose = true;
    }

}
