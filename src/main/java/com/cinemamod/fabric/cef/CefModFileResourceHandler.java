package com.cinemamod.fabric.cef;

import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandler;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

public class CefModFileResourceHandler implements CefResourceHandler {

    @Override
    public boolean processRequest(CefRequest request, CefCallback callback) {
        System.out.println(request.getURL());
        return false;
    }

    @Override
    public void getResponseHeaders(CefResponse response, IntRef responseLength, StringRef redirectUrl) {

    }

    @Override
    public boolean readResponse(byte[] dataOut, int bytesToRead, IntRef bytesRead, CefCallback callback) {
        return false;
    }

    @Override
    public void cancel() {

    }

}
