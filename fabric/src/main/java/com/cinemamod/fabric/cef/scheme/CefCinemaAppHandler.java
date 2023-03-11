package com.cinemamod.fabric.cef.scheme;

import com.cinemamod.fabric.CinemaMod;
import org.cef.CefApp;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefSchemeHandlerFactory;
import org.cef.callback.CefSchemeRegistrar;
import org.cef.handler.CefAppHandlerAdapter;
import org.cef.handler.CefResourceHandler;
import org.cef.network.CefRequest;

public class CefCinemaAppHandler extends CefAppHandlerAdapter {

    public CefCinemaAppHandler(String[] args) {
        super(args);
    }

    @Override
    public void onRegisterCustomSchemes(CefSchemeRegistrar registrar) {
        if (!registrar.addCustomScheme(VideoServiceResourceHandler.SCHEME, true, false, false, false, true, false, false)) {
            CinemaMod.LOGGER.warn("Unable to register " + VideoServiceResourceHandler.SCHEME + " scheme");
        }
    }

    @Override
    public void onContextInitialized() {
        CefApp cefApp = CefApp.getInstance();
        cefApp.registerSchemeHandlerFactory(VideoServiceResourceHandler.SCHEME, VideoServiceResourceHandler.DOMAIN, new CefCinemaSchemeHandlerFactory());
    }

    @Override
    public void onScheduleMessagePumpWork(long delay_ms) {
        // Do nothing
    }

    private static class CefCinemaSchemeHandlerFactory implements CefSchemeHandlerFactory {
        @Override
        public CefResourceHandler create(CefBrowser browser, CefFrame frame, String schemeName, CefRequest request) {
            if (schemeName.equals(VideoServiceResourceHandler.SCHEME)) {
                return new VideoServiceResourceHandler();
            }
            return null;
        }
    }

}
