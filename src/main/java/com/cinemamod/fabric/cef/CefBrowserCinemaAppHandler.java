package com.cinemamod.fabric.cef;

import com.cinemamod.fabric.CinemaMod;
import org.cef.callback.CefSchemeRegistrar;
import org.cef.handler.CefAppHandlerAdapter;

public class CefBrowserCinemaAppHandler extends CefAppHandlerAdapter {

    public CefBrowserCinemaAppHandler() {
        super(new String[] {});
    }

    @Override
    public void onRegisterCustomSchemes(CefSchemeRegistrar registrar) {
        if (!registrar.addCustomScheme("cinemamod",
                true,
                false,
                false,
                false,
                true,
                false,
                false)) {
            CinemaMod.LOGGER.warn("Unable to register cinemamod:// scheme");
        }
    }

    @Override
    public void onScheduleMessagePumpWork(long delay_ms) {
        // Do nothing, we handle this ourselves in CefRenderMixin
    }
}
