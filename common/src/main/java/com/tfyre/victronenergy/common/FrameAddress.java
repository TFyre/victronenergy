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
public class FrameAddress extends Frame {
    
    private int id;
    
    public int getId() {
        return id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public FrameAddress withId(final int id) {
        setId(id);
        return this;
    }
    
    @Override
    public Frame withData(byte[] data) {
        super.withData(data);
        setId(data[3] & 0xff);
        return this;
    }
    
    @Override
    public byte[] getRequest() {
        return new byte[]{(byte) 'A', (byte) 0x01, (byte) id};
    }
    
    @Override
    public String toString() {
        return "FrameAddress{" + "id=" + id + '}';
    }
    
}
