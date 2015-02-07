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
public class FrameVersion extends Frame {

    private int version;
    private int mode;

    @Override
    public Frame withData(byte[] data) {
        super.withData(data);
        version = ((data[5] & 0xff) << 24)
                + ((data[4] & 0xff) << 16)
                + ((data[3] & 0xff) << 8)
                + (data[2] & 0xff);
        mode = data[6];
        return this;
    }

    public String getModeString() {
        switch (mode) {
            case 'W':
                return "VE 9-bit RS485";
            case 'B':
                return "Not Set";
            default:
                return "Address: " + mode;
        }
    }

    @Override
    public String toString() {
        return "FrameVersion{" + "version=" + version + ", mode=" + mode + ", modeString=" + getModeString() + '}';
    }

    @Override
    public byte[] getRequest() {
        return new byte[]{(byte) 'V'};
    }

}
