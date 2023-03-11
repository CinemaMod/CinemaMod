package com.cinemamod.fabric;

import com.cinemamod.fabric.screen.Screen;
import net.minecraft.client.MinecraftClient;

public class WindowFocusMuteThread extends Thread {

    private boolean previousState;

    public WindowFocusMuteThread() {
        setDaemon(true);
        setName("window-focus-cef-mute-thread");
    }

    @Override
    public void run() {
        while (MinecraftClient.getInstance() != null) {
            if (CinemaModClient.getInstance().getVideoSettings().isMuteWhenAltTabbed()) {
                if (MinecraftClient.getInstance().isWindowFocused() && !previousState) {
                    // if currently focused and was previously not focused
                    for (Screen screen : CinemaModClient.getInstance().getScreenManager().getScreens()) {
                        screen.setVideoVolume(CinemaModClient.getInstance().getVideoSettings().getVolume());
                    }
                } else if (!MinecraftClient.getInstance().isWindowFocused() && previousState) {
                    // if not focused and was previous focused
                    for (Screen screen : CinemaModClient.getInstance().getScreenManager().getScreens()) {
                        screen.setVideoVolume(0f);
                    }
                }

                previousState = MinecraftClient.getInstance().isWindowFocused();
            }

            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
