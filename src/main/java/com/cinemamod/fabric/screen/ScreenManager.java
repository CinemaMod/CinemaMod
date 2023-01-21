package com.cinemamod.fabric.screen;

import net.minecraft.util.math.BlockPos;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ScreenManager {

    private final ConcurrentHashMap<BlockPos, Screen> screens;

    public ScreenManager() {
        screens = new ConcurrentHashMap<>();
    }

    public Collection<Screen> getScreens() {
        return screens.values();
    }

    public void registerScreen(Screen screen) {
        if (screens.containsKey(screen.getPos())) {
            Screen old = screens.get(screen.getPos());
            old.unregister();
            old.closeBrowser();
        }

        screen.register();

        screens.put(screen.getPos(), screen);
    }

    public Screen getScreen(BlockPos pos) {
        return screens.get(pos);
    }

    // Used for CefClient LoadHandler
    public Screen getScreen(int browserId) {
        for (Screen screen : screens.values()) {
            if (screen.hasBrowser()) {
                if (screen.getBrowser().getIdentifier() == browserId) {
                    return screen;
                }
            }
        }

        return null;
    }

    public boolean hasActiveScreen() {
        for (Screen screen : screens.values()) {
            if (screen.hasBrowser()) {
                return true;
            }
        }

        return false;
    }

    public void unloadAll() {
        for (Screen screen : screens.values()) {
            screen.closeBrowser();
            screen.unregister();
        }

        screens.clear();
    }

    public void updateAll() {
        for (Screen screen : screens.values()) {
            if (screen.hasBrowser()) {
                screen.getBrowser().update();
            }
        }
    }

}
