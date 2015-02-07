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
public class FrameLED extends Frame {

    private int on;
    private int blink;

    public int getOn() {
        return on;
    }

    public void setOn(final int on) {
        this.on = on;
    }

    public FrameLED withOn(final int on) {
        setOn(on);
        return this;
    }

    public int getBlink() {
        return blink;
    }

    public void setBlink(final int blink) {
        this.blink = blink;
    }

    public FrameLED withBlink(final int blink) {
        setBlink(blink);
        return this;
    }

    @Override
    public byte[] getRequest() {
        return new byte[]{(byte) 'L'};
    }

    @Override
    public Frame withData(byte[] data) {
        super.withData(data);
        setOn(data[2] & 0xff);
        setBlink(data[3] & 0xff);
        return this;
    }

    private String getLedString(final int data) {
        final StringBuilder sb = new StringBuilder();
        if ((data & 0x01) == 0x01) {
            sb.append("Mains");
        }
        if ((data & 0x02) == 0x02) {
            sb.append("Absorption");
        }
        if ((data & 0x04) == 0x04) {
            sb.append("Bulk");
        }
        if ((data & 0x08) == 0x08) {
            sb.append("Float");
        }
        if ((data & 0x10) == 0x10) {
            sb.append("Inverter");
        }
        if ((data & 0x20) == 0x20) {
            sb.append("Overload");
        }
        if ((data & 0x40) == 0x40) {
            sb.append("Low Battery");
        }
        if ((data & 0x80) == 0x80) {
            sb.append("Temperature");
        }
        return sb.toString();
    }

    public String getOnString() {
        return getLedString(on);
    }

    public String getBlinkString() {
        return getLedString(blink);
    }

    @Override
    public String toString() {
        return "FrameLED{" + "on=" + on + ", onS=" + getOnString() + ", blink=" + blink + ", blinkS=" + getBlinkString() + '}';
    }

}
