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
public class FrameReset extends Frame {

    @Override
    public byte[] getRequest() {
        return new byte[]{(byte) 'R'};
    }

}
