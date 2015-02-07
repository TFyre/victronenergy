/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tfyre.victronenergy.common;

import java.text.SimpleDateFormat;
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

    private FrameInfoAC frameInfoAC;
    private long frameInfoACLast;
    private FrameInfoDC frameInfoDC;
    private long frameInfoDCLast;
    private FrameLED frameLED;
    private long frameLEDLast;

    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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

    public FrameInfoAC getFrameInfoAC() {
        return frameInfoAC;
    }

    public String getFrameInfoACData() {
        final StringBuilder sb = new StringBuilder();
        final FrameInfoAC frame = getFrameInfoAC();
        sb.append(String.format("LastRun: %s\n", sdf.format(frameInfoACLast)));
        sb.append(String.format("Input Voltage: %.2f\n", getScaledValue1(RAMVariable.UMAIN, frame.getVoltageFactor())));
        sb.append(String.format("Input Current: %.2f\n", getScaledValue3(RAMVariable.IMAIN, frame.getCurrentFactor(), frame.getBackfeedFactor())));
        sb.append(String.format("Inverter Voltage: %.2f\n", getScaledValue1(RAMVariable.UINVERTER, frame.getInverterVoltageFactor())));
        sb.append(String.format("Inverter Current: %.2f\n", getScaledValue3(RAMVariable.IINVERTER, frame.getInverterCurrentFactor(), frame.getInverterFactor())));
        sb.append(String.format("Input Freq: %.2f\n", getScaledValue2(RAMVariable.MTIME, frame.getPeriodFactor())));
        return sb.toString();
    }

    public void setFrameInfoAC(final FrameInfoAC frameInfoAC) {
        this.frameInfoAC = frameInfoAC;
        this.frameInfoACLast = System.currentTimeMillis();
    }

    public DeviceSettings withFrameInfoAC(final FrameInfoAC frameInfoAC) {
        setFrameInfoAC(frameInfoAC);
        return this;
    }

    public FrameInfoDC getFrameInfoDC() {
        return frameInfoDC;
    }

    public String getFrameInfoDCData() {
        final StringBuilder sb = new StringBuilder();
        final FrameInfoDC frame = getFrameInfoDC();
        sb.append(String.format("LastRun: %s\n", sdf.format(frameInfoDCLast)));
        sb.append(String.format("DC Voltage: %.2f\n", getScaledValue1(RAMVariable.UBAT, frame.getVoltageFactor())));
        sb.append(String.format("DC Inverter: %.2f\n", getScaledValue1(RAMVariable.IBAT, frame.getCurrentInverterFactor())));
        sb.append(String.format("DC Charger: %.2f\n", getScaledValue1(RAMVariable.IBAT, frame.getCurrentChargerFactor())));
        sb.append(String.format("DC Freq: %.2f\n", getScaledValue2(RAMVariable.ITIME, frame.getPeriodFactor())));
        return sb.toString();
    }

    public void setFrameInfoDC(final FrameInfoDC frameInfoDC) {
        this.frameInfoDC = frameInfoDC;
        this.frameInfoDCLast = System.currentTimeMillis();
    }

    public DeviceSettings withFrameInfoDC(final FrameInfoDC frameInfoDC) {
        setFrameInfoDC(frameInfoDC);
        return this;
    }

    public FrameLED getFrameLED() {
        return frameLED;
    }

    public String getFrameLEDData() {
        final StringBuilder sb = new StringBuilder();
        final FrameLED frame = getFrameLED();
        sb.append(String.format("LastRun: %s\n", sdf.format(frameLEDLast)));
        sb.append(String.format("On: %s\n", frame.getOnString()));
        sb.append(String.format("Blinking: %s\n", frame.getBlinkString()));
        return sb.toString();

    }

    public void setFrameLED(final FrameLED frameLED) {
        this.frameLED = frameLED;
        this.frameLEDLast = System.currentTimeMillis();
    }

    public DeviceSettings withFrameLED(final FrameLED frameLED) {
        setFrameLED(frameLED);
        return this;
    }

    public long getFrameLEDLast() {
        return frameLEDLast;
    }

    public long getFrameInfoACLast() {
        return frameInfoACLast;
    }

    public long getFrameInfoDCLast() {
        return frameInfoDCLast;
    }
}
