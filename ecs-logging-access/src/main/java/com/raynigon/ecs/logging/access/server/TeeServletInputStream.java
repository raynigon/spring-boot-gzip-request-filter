package com.raynigon.ecs.logging.access.server;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class TeeServletInputStream extends ServletInputStream {

    private byte[] content;
    private ByteArrayInputStream in;

    TeeServletInputStream(HttpServletRequest request) {
        duplicateInputStream(request);
    }

    private void duplicateInputStream(HttpServletRequest request) {
        ServletInputStream originalSIS = null;
        try {
            originalSIS = request.getInputStream();
            content = IOUtils.toByteArray(request.getInputStream());
            in = new ByteArrayInputStream(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(originalSIS);
        }
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }

    void closeStream(ServletInputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    byte[] getInputBuffer() {
        return content;
    }

    @Override
    public boolean isFinished() {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public boolean isReady() {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public void setReadListener(ReadListener listener) {
        throw new RuntimeException("Not yet implemented");
    }
}

