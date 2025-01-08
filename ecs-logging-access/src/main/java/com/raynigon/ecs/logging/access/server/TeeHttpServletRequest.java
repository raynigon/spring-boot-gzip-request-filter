package com.raynigon.ecs.logging.access.server;

import ch.qos.logback.access.common.AccessConstants;
import ch.qos.logback.access.common.servlet.Util;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TeeHttpServletRequest extends HttpServletRequestWrapper {

    private TeeServletInputStream inStream;
    private BufferedReader reader;
    boolean postedParametersMode = false;

    TeeHttpServletRequest(HttpServletRequest request) {
        super(request);
        // we can't access the input stream and access the request parameters
        // at the same time
        if (Util.isFormUrlEncoded(request)) {
            postedParametersMode = true;
        } else {
            inStream = new TeeServletInputStream(request);
            // add the contents of the input buffer as an attribute of the request
            request.setAttribute(AccessConstants.LB_INPUT_BUFFER, inStream.getInputBuffer());
            reader = new BufferedReader(new InputStreamReader(inStream));
        }

    }

    byte[] getInputBuffer() {
        if (postedParametersMode) {
            throw new IllegalStateException("Call disallowed in postedParametersMode");
        }
        return inStream.getInputBuffer();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (!postedParametersMode) {
            return inStream;
        } else {
            return super.getInputStream();
        }
    }

    //

    @Override
    public BufferedReader getReader() throws IOException {
        if (!postedParametersMode) {
            return reader;
        } else {
            return super.getReader();
        }
    }

    public boolean isPostedParametersMode() {
        return postedParametersMode;
    }
}
