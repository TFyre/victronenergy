/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tfyre.victronenergy.common;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author acid
 */
public class Writer extends Common {

    private static final Logger LOG = Logger.getLogger(Writer.class.getName());

    private final Queue<Frame> queue = new java.util.concurrent.LinkedBlockingQueue<>();
    private final OutputStream socket_os;

    public Writer(final Socket socket, final OutputStream socket_os) {
        super(socket);
        this.socket_os = socket_os;
        getThread().setName("writer");
    }

    public void addFrame(final Frame frame) {
        synchronized (queue) {
            queue.add(frame);
            queue.notifyAll();
        }
    }

    private byte getCRC(final byte[] data) {
        int crc = 0;
        for (int i = 0; i < data.length; i++) {
            crc += data[i];
        }
        return (byte) (256 - (crc % 256));
    }

    @Override
    public void run() {
        while (true) {
            synchronized (queue) {
                try {
                    if (queue.isEmpty()) {
                        queue.wait();
                    }
                } catch (InterruptedException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
            final Frame frame = queue.poll();
            if (frame == null) {
                continue;
            }
            try {
                final byte[] _frame = frame.getRequest();
                final byte[] _data = new byte[_frame.length + 3];
                _data[0] = (byte) (_frame.length + 1);
                _data[1] = (byte) 0xff;
                System.arraycopy(_frame, 0, _data, 2, _frame.length);
                _data[_data.length - 1] = getCRC(_data);
                System.out.println("write: " + toHex(_data));
                socket_os.write(_data);
                //Thread.sleep(1000);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
                return;
            }

        }
    }

}
