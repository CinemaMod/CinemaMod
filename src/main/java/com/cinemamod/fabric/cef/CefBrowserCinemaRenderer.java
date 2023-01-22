package com.cinemamod.fabric.cef;

import com.mojang.blaze3d.systems.RenderSystem;

import java.awt.*;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8_REV;

public class CefBrowserCinemaRenderer {

    private final boolean transparent;
    private int[] textureID = new int[1];
    private int width = 0;
    private int height = 0;

    protected CefBrowserCinemaRenderer(boolean transparent) {
        this.transparent = transparent;
        initialize();
    }

    public int getTextureID() {
        return textureID[0];
    }

    protected void initialize() {
        RenderSystem.enableTexture();
        textureID[0] = glGenTextures();
        RenderSystem.bindTexture(textureID[0]);
        RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        RenderSystem.bindTexture(0);
    }

    protected void cleanup() {
        if (textureID[0] != 0) {
            glDeleteTextures(textureID[0]);
        }
    }

    protected void onPaint(Rectangle[] dirtyRects, ByteBuffer buffer, int width, int height, boolean rerender) {
        if (transparent) {
            // Enable alpha blending
            RenderSystem.enableBlend();
        }

        // Enable 2D textures
        RenderSystem.enableTexture();

        RenderSystem.bindTexture(textureID[0]);

        int lastWidth = this.width;
        int lastHeight = this.height;

        this.width = width;
        this.height = height;

        RenderSystem.pixelStore(GL_UNPACK_ROW_LENGTH, this.width);

        if (rerender || lastWidth != this.width || lastHeight != this.height) {
            RenderSystem.pixelStore(GL_UNPACK_SKIP_PIXELS, 0);
            RenderSystem.pixelStore(GL_UNPACK_SKIP_ROWS, 0);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0,
                    GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, buffer);
        } else {
            for (Rectangle rect : dirtyRects) {
                RenderSystem.pixelStore(GL_UNPACK_SKIP_PIXELS, rect.x);
                RenderSystem.pixelStore(GL_UNPACK_SKIP_ROWS, rect.y);
                glTexSubImage2D(GL_TEXTURE_2D, 0, rect.x, rect.y, rect.width,
                        rect.height, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, buffer);
            }
        }
    }

}
