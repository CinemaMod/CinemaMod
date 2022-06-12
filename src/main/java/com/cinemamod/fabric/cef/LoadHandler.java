package com.cinemamod.fabric.cef;

import com.cinemamod.fabric.CinemaMod;
import com.cinemamod.fabric.CinemaModClient;
import com.cinemamod.fabric.screen.Screen;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandler;
import org.cef.network.CefRequest;

public class LoadHandler implements CefLoadHandler {

    @Override
    public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {
    }

    @Override
    public void onLoadStart(CefBrowser browser, CefFrame frame, CefRequest.TransitionType transitionType) {
    }

    @Override
    public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
        Screen screen = CinemaModClient.getInstance().getScreenManager().getScreen(browser.getIdentifier());

        screen.startVideo();

        if (screen.isMuted()) {
            screen.setVideoVolume(0);
        } else {
            screen.setVideoVolume(CinemaModClient.getInstance().getVideoSettings().getVolume());
        }
    }

    @Override
    public void onLoadError(CefBrowser browser, CefFrame frame, ErrorCode errorCode, String errorText, String failedUrl) {
        CinemaMod.LOGGER.warn("Load error: " + errorText);
    }

}
