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
public class FrameInfo extends Frame {

    private int type;
    private Phase phase;

    public int getType() {
        return type;
    }

    public void setType(final int type) {
        this.type = type;
    }

    public FrameInfo withType(final int type) {
        setType(type);
        return this;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(final Phase phase) {
        this.phase = phase;
    }

    public FrameInfo withPhase(final Phase phase) {
        setPhase(phase);
        return this;
    }

    @Override
    public Frame withData(byte[] data) {
        super.withData(data); //To change body of generated methods, choose Tools | Templates.
        return this;
    }

    @Override
    public byte[] getRequest() {
        return new byte[]{(byte) 'F', (byte) type};
    }

}
