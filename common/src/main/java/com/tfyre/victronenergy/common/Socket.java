/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tfyre.victronenergy.common;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author acid
 */
public class Socket implements Runnable {

    private static final Logger LOG = Logger.getLogger(Socket.class.getName());

    java.net.Socket socket;
    private Writer writer;
    private Reader reader;

    private final Thread thread;
    private final CallbackInterface callback;

    private final String host;
    private final int port;

    public Socket(final CallbackInterface callback, final String host, final int port) {
        this.host = host;
        this.port = port;
        this.thread = new Thread(this);
        this.thread.setName("socket");
        this.thread.setDaemon(true);
        this.callback = callback;
    }

    public Thread getThread() {
        return thread;
    }

    private void open() throws IOException {
        socket = new java.net.Socket(host, port);
        writer = new Writer(this, socket.getOutputStream());
        writer.getThread().start();
        reader = new Reader(this, socket.getInputStream());
        reader.getThread().start();
    }

    protected void addReaderFrame(final Frame frame) {
        callback.processFrame(frame);
    }

    public void addWriterFrame(final Frame frame) {
        writer.addFrame(frame);
    }

    @Override
    public void run() {
        try {
            open();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            return;
        }
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

}
