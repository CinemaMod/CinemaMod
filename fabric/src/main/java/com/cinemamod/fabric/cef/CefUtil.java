package com.cinemamod.fabric.cef;

import com.cinemamod.fabric.CinemaModClient;
import com.cinemamod.fabric.cef.scheme.CefCinemaAppHandler;
import com.cinemamod.fabric.screen.Screen;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;

public final class CefUtil {

    private CefUtil() {}

    private static boolean init;
    private static CefApp cefAppInstance;
    private static CefClient cefClientInstance;

    public static boolean init() {
        String[] cefSwitches = new String[]{
                "--autoplay-policy=no-user-gesture-required",
                "--disable-web-security"
        };

        if (!CefApp.startup(cefSwitches)) {
            return false;
        }

        CefSettings cefSettings = new CefSettings();
        cefSettings.windowless_rendering_enabled = true;
        cefSettings.background_color = cefSettings.new ColorType(0, 255, 255, 255);

        cefAppInstance = CefApp.getInstance(cefSwitches, cefSettings);

//        CefApp.addAppHandler(new CefCinemaAppHandler(cefSwitches));

        cefClientInstance = cefAppInstance.createClient();
        cefClientInstance.addLoadHandler(new CefBrowserCinemaLoadHandler());

        return init = true;
    }

    public static boolean isInit() {
        return init;
    }

    public static CefApp getCefApp() {
        return cefAppInstance;
    }

    public static CefClient getCefClient() {
        return cefClientInstance;
    }

    public static CefBrowserCinema createBrowser(String startUrl, int widthPx, int heightPx) {
        if (!init) return null;
        CefBrowserCinema browser = new CefBrowserCinema(cefClientInstance, startUrl, false, null);
        browser.setCloseAllowed();
        browser.createImmediately();
        browser.resize(widthPx, heightPx);
        return browser;
    }

    public static CefBrowserCinema createBrowser(String startUrl, Screen screen) {
        if (!init) return null;
        CefBrowserCinema browser = new CefBrowserCinema(cefClientInstance, startUrl, true, null);
        browser.setCloseAllowed();
        browser.createImmediately();
        // Adjust screen size
        {
            float widthBlocks = screen.getWidth();
            float heightBlocks = screen.getHeight();
            float scale = widthBlocks / heightBlocks;
            int height = CinemaModClient.getInstance().getVideoSettings().getBrowserResolution();
            int width = (int) Math.floor(height * scale);
            browser.resize(width, height);
        }
        return browser;
    }

}
