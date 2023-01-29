package com.cinemamod.fabric.cef.scheme;

import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandlerAdapter;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

public class VideoServiceResourceHandler extends CefResourceHandlerAdapter {

    public static final String SCHEME = "video";
    public static final String DOMAIN = "";

    @Override
    public synchronized boolean processRequest(CefRequest request, CefCallback callback) {
        callback.Continue();
        return true;
    }

    @Override
    public synchronized void getResponseHeaders(CefResponse response, IntRef responseLength, StringRef redirectUrl) {
        responseLength.set(0);
        response.setMimeType("text/plain");
        response.setStatus(200);
    }

    @Override
    public synchronized boolean readResponse(byte[] dataOut, int bytesToRead, IntRef bytesRead, CefCallback callback) {
        callback.Continue();
        return true;
    }

}
