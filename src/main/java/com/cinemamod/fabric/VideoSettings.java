package com.cinemamod.fabric;

import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

public class VideoSettings {

    private static final Path PATH = MinecraftClient.getInstance().runDirectory
            .toPath()
            .resolve("config")
            .resolve(CinemaMod.MODID)
            .resolve(CinemaMod.MODID + ".properties");

    private float volume;
    private boolean muteWhenAltTabbed;
    private boolean hideCrosshair;
    private int browserResolution;

    public VideoSettings(float volume, boolean muteWhenAltTabbed, boolean hideCrosshair, int browserResolution) {
        this.volume = volume;
        this.muteWhenAltTabbed = muteWhenAltTabbed;
        this.hideCrosshair = hideCrosshair;
        this.browserResolution = browserResolution;
    }

    public VideoSettings() {
        volume = 1.0f;
        muteWhenAltTabbed = true;
        hideCrosshair = true;
        browserResolution = 720;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public boolean isMuteWhenAltTabbed() {
        return muteWhenAltTabbed;
    }

    public void setMuteWhenAltTabbed(boolean muteWhenAltTabbed) {
        this.muteWhenAltTabbed = muteWhenAltTabbed;
    }

    public boolean isHideCrosshair() {
        return hideCrosshair;
    }

    public void setHideCrosshair(boolean hideCrosshair) {
        this.hideCrosshair = hideCrosshair;
    }

    public int getBrowserResolution() {
        return browserResolution;
    }

    public void setNextBrowserResolution() {
        if (browserResolution <= 240) {
            browserResolution = 360;
        } else if (browserResolution <= 360) {
            browserResolution = 480;
        } else if (browserResolution <= 480) {
            browserResolution = 720;
        } else if (browserResolution <= 720) {
            browserResolution = 1080;
        } else if (browserResolution >= 1080) {
            browserResolution = 240;
        }
    }

    public void saveAsync() {
        CompletableFuture.runAsync(() -> {
            try {
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void save() throws IOException {
        File file = PATH.toFile();

        file.getParentFile().mkdirs();

        if (!file.exists()) {
            file.createNewFile();
        }

        Properties properties = new Properties();
        properties.setProperty("volume", String.valueOf(volume));
        properties.setProperty("mute-while-alt-tabbed", String.valueOf(muteWhenAltTabbed));
        properties.setProperty("hide-crosshair-while-screen-loaded", String.valueOf(hideCrosshair));
        properties.setProperty("browser-resolution", String.valueOf(browserResolution));

        try (FileOutputStream output = new FileOutputStream(file)) {
            properties.store(output, null);
        }
    }

    public void load() throws IOException {
        File file = PATH.toFile();

        if (!file.exists()) {
            save();
        }

        Properties properties = new Properties();

        try (FileInputStream input = new FileInputStream(file)) {
            properties.load(input);
        }

        try {
            volume = Float.parseFloat(properties.getProperty("volume"));
            muteWhenAltTabbed = properties.getProperty("mute-while-alt-tabbed").equalsIgnoreCase("true");
            hideCrosshair = properties.getProperty("hide-crosshair-while-screen-loaded").equalsIgnoreCase("true");
            browserResolution = Integer.parseInt(properties.getProperty("browser-resolution"));
        } catch (Exception e) {
            file.delete();
            save();
        }
    }

}
