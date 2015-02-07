/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tfyre.victronenergy.common;

import java.util.logging.Logger;

/**
 *
 * @author acid
 */
public abstract class Frame {

    private static final Logger LOG = Logger.getLogger(Frame.class.getName());

    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(final byte[] data) {
        this.data = data;
    }

    public abstract byte[] getRequest();

    public Frame withData(final byte[] data) {
        setData(data);
        return this;
    }

    public static Frame fromData20(final byte[] data) {
        switch (data[5]) {
            case (byte) 0x0c:
                return new FrameInfoDC()
                        .withData(data);
            default:
                return new FrameInfoAC()
                        .withData(data);
        }
    }

    public static Frame fromDataFF(final byte[] data) {
        switch (data[1]) {
            case 'A':
                return new FrameAddress()
                        .withData(data);
            case 'L':
                return new FrameLED()
                        .withData(data);
            case 'V':
                return new FrameVersion()
                        .withData(data);
            case 'W':
                return new FrameCommand()
                        .withData(data);
            default:
                LOG.severe(String.format("Invalid Frame: %s", Common.toHex(data)));
                System.exit(1);
                throw new RuntimeException("Invalid Frame");
        }
    }

    public static Frame fromData(final byte[] data) {
        switch (data[0]) {
            case (byte) 0x20:
                return fromData20(data);
            case (byte) 0xff:
                return fromDataFF(data);
            default:
                LOG.severe(String.format("Invalid Frame: %s", Common.toHex(data)));
                System.exit(1);
                throw new RuntimeException("Invalid Frame");
        }
    }

}
