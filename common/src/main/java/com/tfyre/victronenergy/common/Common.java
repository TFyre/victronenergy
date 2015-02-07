/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tfyre.victronenergy.common;

/**
 *
 * @author acid
 */
public abstract class Common implements Runnable {

    private final Socket socket;
    private final Thread thread;

    public Common(Socket socket) {
        this.thread = new Thread(this);
        this.thread.setName("common");
        this.thread.setDaemon(true);
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public Thread getThread() {
        return thread;
    }

    public static String toHex(final byte[] data) {
        final StringBuilder sb = new StringBuilder();
        for (final byte b : data) {
            if ((b < 32) || (b > 126)) {
                sb.append(String.format("[%02X]", b));
            } else {
                sb.append(String.format("[%02X]", b));
                //sb.append((char) b);
            }
        }
        return sb.toString();
    }
}
