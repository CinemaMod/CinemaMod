package com.cinemamod.fabric.cef;

import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefBrowser_N;
import org.cef.browser.CefRequestContext;
import org.cef.callback.CefDragData;
import org.cef.handler.CefRenderHandler;
import org.cef.handler.CefScreenInfo;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

public class CefBrowserCinema extends CefBrowser_N implements CefRenderHandler {

    private final CefImageData imageData = new CefImageData();
    private int[] textureID = new int[1];

    public CefBrowserCinema(CefClient client, String url) {
        this(client, url, null, null, null);
    }

    private CefBrowserCinema(CefClient client, String url, CefRequestContext context, CefBrowser_N parent, Point inspectAt) {
        super(client, url, context, parent, inspectAt);
    }

    @Override
    public Rectangle getViewRect(CefBrowser browser) {
        return null;
    }

    @Override
    public boolean getScreenInfo(CefBrowser browser, CefScreenInfo screenInfo) {
        return false;
    }

    @Override
    public Point getScreenPoint(CefBrowser browser, Point viewPoint) {
        return null;
    }

    @Override
    public void onPopupShow(CefBrowser browser, boolean show) {

    }

    @Override
    public void onPopupSize(CefBrowser browser, Rectangle size) {

    }

    @Override
    public void onPaint(CefBrowser browser, boolean popup, Rectangle[] dirtyRects, ByteBuffer buffer, int width, int height) {
        System.out.println("On paint");
//        if (popup) {
//            return;
//        }
//
//        final int size = (width * height) << 2;
//
//        synchronized (imageData) {
//            if (buffer.limit() > size) {
//                // TODO:
//            } else {
//                if (imageData.hasFrame) // The previous frame was not uploaded to GL texture, so we skip it and render this on instead
//                    imageData.fullReRender = true;
//
//                if (imageData.buffer == null || size != imageData.buffer.capacity()) // This only happens when the browser gets resized
//                    imageData.buffer = BufferUtils.createByteBuffer(size);
//
//                imageData.buffer.position(0);
//                imageData.buffer.limit(buffer.limit());
//                buffer.position(0);
//                imageData.buffer.put(buffer);
//                imageData.buffer.position(0);
//
//                imageData.width = width;
//                imageData.height = height;
//                imageData.dirtyRects = dirtyRects;
//                imageData.hasFrame = true;
//            }
//        }
    }

    @Override
    public boolean onCursorChange(CefBrowser browser, int cursorType) {
        return false;
    }

    @Override
    public boolean startDragging(CefBrowser browser, CefDragData dragData, int mask, int x, int y) {
        return false;
    }

    @Override
    public void updateDragCursor(CefBrowser browser, int operation) {

    }

    @Override
    public void createImmediately() {
        createBrowser(getClient(), 0, getUrl(), true, true, null, getRequestContext());
    }

    @Override
    public Component getUIComponent() {
        return null;
    }

    @Override
    public CompletableFuture<BufferedImage> createScreenshot(boolean nativeResolution) {
        return null;
    }

    @Override
    protected CefBrowser_N createDevToolsBrowser(CefClient client, String url, CefRequestContext context, CefBrowser_N parent, Point inspectAt) {
        return null;
    }

    public int getTextureID() {
        return textureID[0];
    }

    public void resize(int width, int height) {
        wasResized(width, height);
    }

    public void close() {
        close(true);
        // TODO: cleanup renderer
    }

}
