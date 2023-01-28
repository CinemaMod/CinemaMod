package com.cinemamod.fabric.cef;

import java.awt.Component;
import java.awt.event.KeyEvent;

public class CefBrowserKeyEvent extends KeyEvent {

    private static final long serialVersionUID = 1L;
    private long scancode = 0;

    public CefBrowserKeyEvent(Component source, int id, long when, int modifiers, int keyCode, char keyChar, int keyLocation) {
        super(source, id, when, modifiers, keyCode, keyChar, keyLocation);
    }

    public CefBrowserKeyEvent(Component source, int id, long when, int modifiers, int keyCode, char keyChar, long scanCode) {
        super(source, id, when, modifiers, keyCode, keyChar);
        this.scancode = scanCode;
    }
}
