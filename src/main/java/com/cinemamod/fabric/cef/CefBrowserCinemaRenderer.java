package com.cinemamod.fabric.cef;

import com.mojang.blaze3d.systems.RenderSystem;

import java.awt.*;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8_REV;

public class CefBrowserCinemaRenderer {
    private boolean transparent_;
    public int[] texture_id_ = new int[1];
    private int view_width_ = 0;
    private int view_height_ = 0;
    private float spin_x_ = 0f;
    private float spin_y_ = 0f;
    private Rectangle popup_rect_ = new Rectangle(0, 0, 0, 0);
    private Rectangle original_popup_rect_ = new Rectangle(0, 0, 0, 0);
    private boolean use_draw_pixels_ = false;

    protected CefBrowserCinemaRenderer(boolean transparent) {
        transparent_ = transparent;
        initialize();
    }

    protected void initialize() {
        RenderSystem.enableTexture();
        texture_id_[0] = glGenTextures();
        RenderSystem.bindTexture(texture_id_[0]);
        RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        RenderSystem.bindTexture(0);
    }

    protected void cleanup() {
        if (texture_id_[0] != 0) {
            glDeleteTextures(texture_id_[0]);
        }
    }

    protected void onPopupSize(Rectangle rect) {
        if (rect.width <= 0 || rect.height <= 0) return;
        original_popup_rect_ = rect;
        popup_rect_ = getPopupRectInWebView(original_popup_rect_);
    }

    protected Rectangle getPopupRect() {
        return (Rectangle) popup_rect_.clone();
    }

    protected Rectangle getPopupRectInWebView(Rectangle original_rect) {
        Rectangle rc = original_rect;
        // if x or y are negative, move them to 0.
        if (rc.x < 0) rc.x = 0;
        if (rc.y < 0) rc.y = 0;
        // if popup goes outside the view, try to reposition origin
        if (rc.x + rc.width > view_width_) rc.x = view_width_ - rc.width;
        if (rc.y + rc.height > view_height_) rc.y = view_height_ - rc.height;
        // if x or y became negative, move them to 0 again.
        if (rc.x < 0) rc.x = 0;
        if (rc.y < 0) rc.y = 0;
        return rc;
    }

    protected void clearPopupRects() {
        popup_rect_.setBounds(0, 0, 0, 0);
        original_popup_rect_.setBounds(0, 0, 0, 0);
    }

    protected void onPaint(boolean popup, Rectangle[] dirtyRects, ByteBuffer buffer, int width, int height, boolean completeReRender) {
//        if (use_draw_pixels_) {
//            glRasterPos2f(-1, 1);
//            glPixelZoom(1, -1);
//            glDrawPixels(width, height, GL_BGRA, GL_UNSIGNED_BYTE, buffer);
//            return;
//        }

        if (transparent_) {
            // Enable alpha blending.
            RenderSystem.enableBlend();
        }

        // Enable 2D textures.
        RenderSystem.enableTexture();

        RenderSystem.bindTexture(texture_id_[0]);

        if (!popup) {
            int old_width = view_width_;
            int old_height = view_height_;

            view_width_ = width;
            view_height_ = height;

            RenderSystem.pixelStore(GL_UNPACK_ROW_LENGTH, view_width_);

            if (completeReRender || old_width != view_width_ || old_height != view_height_) {
                // Update/resize the whole texture.
                RenderSystem.pixelStore(GL_UNPACK_SKIP_PIXELS, 0);
                RenderSystem.pixelStore(GL_UNPACK_SKIP_ROWS, 0);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, view_width_, view_height_, 0,
                        GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, buffer);
            } else {
                // Update just the dirty rectangles.
                for (int i = 0; i < dirtyRects.length; ++i) {
                    Rectangle rect = dirtyRects[i];
                    RenderSystem.pixelStore(GL_UNPACK_SKIP_PIXELS, rect.x);
                    RenderSystem.pixelStore(GL_UNPACK_SKIP_ROWS, rect.y);
                    glTexSubImage2D(GL_TEXTURE_2D, 0, rect.x, rect.y, rect.width,
                            rect.height, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, buffer);
                }
            }
        } else if (popup && popup_rect_.width > 0 && popup_rect_.height > 0) {
            int skip_pixels = 0, x = popup_rect_.x;
            int skip_rows = 0, y = popup_rect_.y;
            int w = width;
            int h = height;

            // Adjust the popup to fit inside the view.
            if (x < 0) {
                skip_pixels = -x;
                x = 0;
            }
            if (y < 0) {
                skip_rows = -y;
                y = 0;
            }
            if (x + w > view_width_) w -= x + w - view_width_;
            if (y + h > view_height_) h -= y + h - view_height_;

            // Update the popup rectangle.
            RenderSystem.pixelStore(GL_UNPACK_ROW_LENGTH, width);
            RenderSystem.pixelStore(GL_UNPACK_SKIP_PIXELS, skip_pixels);
            RenderSystem.pixelStore(GL_UNPACK_SKIP_ROWS, skip_rows);
            glTexSubImage2D(GL_TEXTURE_2D, 0, x, y, w, h, GL_BGRA,
                    GL_UNSIGNED_INT_8_8_8_8_REV, buffer);
        }

        // Disable 2D textures.
        RenderSystem.disableTexture();

        if (transparent_) {
            RenderSystem.disableBlend();
        }
    }

}
