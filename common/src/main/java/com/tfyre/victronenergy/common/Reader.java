/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tfyre.victronenergy.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author acid
 */
public class Reader extends Common {

    private static final Logger LOG = Logger.getLogger(Reader.class.getName());

    private final ByteArrayOutputStream data = new ByteArrayOutputStream();
    private final InputStream socket_is;

    public Reader(final Socket socket, final InputStream socket_is) {
        super(socket);
        this.socket_is = socket_is;
        getThread().setName("reader");
    }

    private byte[] readBytes(final int _size) throws IOException {
        int sizeTotal = 0;
        final byte[] result = new byte[_size];
        while (sizeTotal < _size) {
            final int read_size = socket_is.read(result, sizeTotal, _size - sizeTotal);
            sizeTotal += read_size;
        }
        return result;
    }

    private boolean doRead() throws IOException {
        if (socket_is.available() > 0) {
            data.write(readBytes(socket_is.available()));
            return true;
        }
        final int size = socket_is.read();
        if (size < 0) {
            return false;
        }
        data.write(size);
        return true;
    }

    private int getCheckSum(final byte[] data, final int start, final int length) {
        int result = 0;
        for (int i = start; i < start + length; i++) {
            result += data[i] & 0xff;
        }
        return result & 0xff;
    }

    private boolean processData() {
        if (data.size() == 0) {
            return false;
        }
        final byte[] _data = data.toByteArray();
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer(String.format("start: %s", toHex(_data)));
        }
        for (int i = 0; i < _data.length; i++) {
            final int b = _data[i] & 0xff;
            if (b == 0) {
                continue;
            }
            final boolean hasLED = (b & 0x80) == 0x80;
            final int len = (b & 0x7f) + 2;
            if (len > (_data.length - i)) {
                continue;
            }
            final int c = getCheckSum(_data, i, len);
            if (c != 0) {
                continue;
            }
            if (LOG.isLoggable(Level.FINER)) {
                LOG.finer(String.format("led: %s", hasLED));
            }
            final byte[] frameData = new byte[len - 2];
            System.arraycopy(_data, i + 1, frameData, 0, len - 2);
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine(toHex(frameData));
            }
            final Frame frame = Frame.fromData(frameData);
            if (frame instanceof FrameInvalid) {
                LOG.severe(toHex(_data));
                continue;
            }
            getSocket().addReaderFrame(frame);
            data.reset();
            data.write(_data, i + b, _data.length - i - len);
            if (LOG.isLoggable(Level.FINER)) {
                LOG.finer(String.format("end: %s", toHex(data.toByteArray())));
            }
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!doRead()) {
                    return;
                }
                while (processData()) {
                    //Continue to processData
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
                return;
            }
        }
    }

}
