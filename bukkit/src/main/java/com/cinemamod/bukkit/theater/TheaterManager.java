package com.cinemamod.bukkit.theater;

import com.cinemamod.bukkit.CinemaModPlugin;
import com.cinemamod.bukkit.theater.screen.PreviewScreen;
import com.cinemamod.bukkit.theater.screen.Screen;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TheaterManager {

    private CinemaModPlugin cinemaModPlugin;
    private List<Theater> theaters;

    public TheaterManager(CinemaModPlugin cinemaModPlugin) {
        this.cinemaModPlugin = cinemaModPlugin;
        theaters = new ArrayList<>();
    }

    public List<Theater> getTheaters() {
        return theaters;
    }

    public void tickTheaters() {
        for (Theater theater : theaters) {
            theater.tick(cinemaModPlugin);
        }
    }

    public Theater getCurrentTheater(Player player) {
        for (Theater theater : theaters) {
            if (theater.isViewer(player)) {
                return theater;
            }
        }

        return null;
    }

    public synchronized void loadFromConfig(ConfigurationSection theaterSection) {
        List<Theater> theaters = new ArrayList<>();

        for (String theaterId : theaterSection.getKeys(false)) {
            String theaterName = theaterSection.getString(theaterId + ".name");
            boolean theaterHidden = theaterSection.getBoolean(theaterId + ".hidden");
            String theaterType = theaterSection.getString(theaterId + ".type");
            String screenWorld = theaterSection.getString(theaterId + ".screen.world");
            int screenX = theaterSection.getInt(theaterId + ".screen.x");
            int screenY = theaterSection.getInt(theaterId + ".screen.y");
            int screenZ = theaterSection.getInt(theaterId + ".screen.z");
            String screenFacing = theaterSection.getString(theaterId + ".screen.facing");
            float screenWidth = (float) theaterSection.getDouble(theaterId + ".screen.width");
            float screenHeight = (float) theaterSection.getDouble(theaterId + ".screen.height");
            boolean screenVisible = theaterSection.getBoolean(theaterId + ".screen.visible");
            boolean screenMuted = theaterSection.getBoolean(theaterId + ".screen.muted");
            Screen screen = new Screen(screenWorld, screenX, screenY, screenZ, screenFacing, screenWidth, screenHeight, screenVisible, screenMuted);

            final Theater theater;

            switch (theaterType) {
                case "public":
                    theater = new PublicTheater(cinemaModPlugin, theaterId, theaterName, theaterHidden, screen);
                    break;
                case "private":
                    theater = new PrivateTheater(cinemaModPlugin, theaterId, theaterName, theaterHidden, screen);
                    break;
                case "static":
                    String staticUrl = theaterSection.getString(theaterId + ".static.url");
                    int staticResWidth;
                    int staticResHeight;
                    if (theaterSection.isSet(theaterId + ".static.res-width") && theaterSection.isSet(theaterId + ".static.res-height")) {
                        staticResWidth = theaterSection.getInt(theaterId + ".static.res-width");
                        staticResHeight = theaterSection.getInt(theaterId + ".static.res-height");
                    } else {
                        staticResWidth = 0;
                        staticResHeight = 0;
                    }
                    theater = new StaticTheater(cinemaModPlugin, theaterId, theaterName, theaterHidden, screen, staticUrl, staticResWidth, staticResHeight);
                    break;
                default:
                    throw new RuntimeException(cinemaModPlugin.getCinemaLanguageConfig().getMessage("unknown-theater-type-for", "Unknown theater type for ") + theaterId);
            }

            if (theaterSection.isSet(theaterId + ".preview-screens")) {
                ConfigurationSection previewScreenSection = theaterSection.getConfigurationSection(theaterId + ".preview-screens");
                for (String previewScreenId : previewScreenSection.getKeys(false)) {
                    String previewScreenWorld = previewScreenSection.getString(previewScreenId + ".world");
                    int previewScreenX = previewScreenSection.getInt(previewScreenId + ".x");
                    int previewScreenY = previewScreenSection.getInt(previewScreenId + ".y");
                    int previewScreenZ = previewScreenSection.getInt(previewScreenId + ".z");
                    String previewScreenFacing = previewScreenSection.getString(previewScreenId + ".facing");
                    PreviewScreen previewScreen = new PreviewScreen(previewScreenWorld, previewScreenX, previewScreenY, previewScreenZ, previewScreenFacing);
                    theater.addPreviewScreen(previewScreen);
                }
            }

            theaters.add(theater);
        }

        this.theaters = theaters;
    }

}
