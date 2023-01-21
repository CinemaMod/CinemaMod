package com.cinemamod.fabric.cef;

import java.awt.*;
import java.nio.ByteBuffer;

public class CefImageData {

    public ByteBuffer buffer;
    public int width;
    public int height;
    public Rectangle[] dirtyRects;
    public boolean hasFrame;
    public boolean fullReRender;

}
