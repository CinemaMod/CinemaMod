package com.cinemamod.fabric.cef;

import java.awt.*;
import java.awt.event.KeyEvent;

public class CefBrowserCinemaKeyEvent extends KeyEvent {

    private long scancode = 0; // https://github.com/CinemaMod/java-cef/blob/6f9ddcb78228fdaac0eacba04f905a6aa97cff9f/native/CefBrowser_N.cpp#L1625

    public CefBrowserCinemaKeyEvent(Component source, int id, long when, int modifiers, int keyCode, char keyChar, int keyLocation) {
        super(source, id, when, modifiers, keyCode, keyChar, keyLocation);
    }

    public CefBrowserCinemaKeyEvent(Component source, int id, long when, int modifiers, int keyCode, char keyChar, long scanCode) {
        super(source, id, when, modifiers, keyCode, keyChar);
        this.scancode = scanCode;
    }

}
