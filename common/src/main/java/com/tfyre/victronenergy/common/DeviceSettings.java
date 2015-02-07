/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tfyre.victronenergy.common;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author acid
 */
public class DeviceSettings {

    private static final Logger LOG = Logger.getLogger(DeviceSettings.class.getName());

    private Map<RAMVariable, FrameCommand> data = new HashMap<>();

    public void setMap(final RAMVariable ramVariable, final FrameCommand frame) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, String.format("%s - Scale [%6f] offSet [%d]", ramVariable.getName(), frame.getFraction(), frame.getOffset()));
        }
        data.put(ramVariable, frame);
    }

    public double getScaledValue1(final RAMVariable ramVariable, final int value) {
        if (!data.containsKey(ramVariable)) {
            return -1;
        }
        final FrameCommand frame = data.get(ramVariable);
        return frame.getFraction() * value;
    }
    
    public double getScaledValue2(final RAMVariable ramVariable, final int value) {
        if (!data.containsKey(ramVariable)) {
            return -1;
        }
        final FrameCommand frame = data.get(ramVariable);
        return (10 / ((value + frame.getOffset()) * frame.getFraction()));        
        
    }

    public double getScaledValue3(final RAMVariable ramVariable, final int value, final int factor) {
        return getScaledValue1(ramVariable, value) * factor;
    }
}
