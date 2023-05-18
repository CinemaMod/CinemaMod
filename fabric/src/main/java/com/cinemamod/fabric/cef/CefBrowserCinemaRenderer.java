package com.cinemamod.fabric.cef;

import com.mojang.blaze3d.systems.RenderSystem;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8_REV;

public class CefBrowserCinemaRenderer {

    private final boolean transparent;
    private final int[] textureID = new int[1];

    protected CefBrowserCinemaRenderer(boolean transparent) {
        this.transparent = transparent;
    }

    public void initialize() {
        // TODO: fixme
//        RenderSystem.enableTexture();
        textureID[0] = glGenTextures();
        RenderSystem.bindTexture(textureID[0]);
        RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        RenderSystem.bindTexture(0);
    }

    public int getTextureID() {
        return textureID[0];
    }

    protected void cleanup() {
        if (textureID[0] != 0) {
            glDeleteTextures(textureID[0]);
            textureID[0] = 0;
        }
    }

    protected void onPaint(ByteBuffer buffer, int width, int height) {
        if (textureID[0] == 0) return;
        if (transparent) RenderSystem.enableBlend();
        // TODO: fixme
//        RenderSystem.enableTexture();
        RenderSystem.bindTexture(textureID[0]);
        RenderSystem.pixelStore(GL_UNPACK_ROW_LENGTH, width);
        RenderSystem.pixelStore(GL_UNPACK_SKIP_PIXELS, 0);
        RenderSystem.pixelStore(GL_UNPACK_SKIP_ROWS, 0);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,
                GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, buffer);
    }

}
