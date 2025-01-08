package com.raynigon.ecs.logging.access.server;


import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.WriteListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TeeServletOutputStream extends ServletOutputStream {

    final ServletOutputStream underlyingStream;
    final ByteArrayOutputStream baosCopy;

    TeeServletOutputStream(ServletResponse httpServletResponse) throws IOException {
        this.underlyingStream = httpServletResponse.getOutputStream();
        baosCopy = new ByteArrayOutputStream();
    }

    byte[] getOutputStreamAsByteArray() {
        return baosCopy.toByteArray();
    }

    @Override
    public void write(int val) throws IOException {
        if (underlyingStream != null) {
            underlyingStream.write(val);
            baosCopy.write(val);
        }
    }

    @Override
    public void write(byte[] byteArray) throws IOException {
        if (underlyingStream == null) {
            return;
        }
        write(byteArray, 0, byteArray.length);
    }

    @Override
    public void write(byte byteArray[], int offset, int length) throws IOException {
        if (underlyingStream == null) {
            return;
        }
        underlyingStream.write(byteArray, offset, length);
        baosCopy.write(byteArray, offset, length);
    }

    @Override
    public void close() throws IOException {
        // If the servlet accessing the stream is using a writer instead of
        // an OutputStream, it will probably call os.close() before calling
        // writer.close. Thus, the underlying output stream will be called
        // before the data sent to the writer could be flushed.
    }

    @Override
    public void flush() throws IOException {
        if (underlyingStream == null) {
            return;
        }
        underlyingStream.flush();
        baosCopy.flush();
    }

    @Override
    public boolean isReady() {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public void setWriteListener(WriteListener listener) {
        throw new RuntimeException("Not yet implemented");
    }
}
