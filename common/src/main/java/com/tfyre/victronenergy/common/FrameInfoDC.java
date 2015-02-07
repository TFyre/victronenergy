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
public class FrameInfoDC extends FrameInfo {

    private int voltageFactor;
    private int currentInverterFactor;
    private int currentChargerFactor;
    private int periodFactor;

    public int getVoltageFactor() {
        return voltageFactor;
    }

    public void setVoltageFactor(final int voltageFactor) {
        this.voltageFactor = voltageFactor;
    }

    public FrameInfoDC withVoltageFactor(final int voltageFactor) {
        setVoltageFactor(voltageFactor);
        return this;

    }

    public int getCurrentInverterFactor() {
        return currentInverterFactor;
    }

    public void setCurrentInverterFactor(final int currentInverterFactor) {
        this.currentInverterFactor = currentInverterFactor;
    }

    public FrameInfoDC withCurrentInverterFactor(final int currentInverterFactor) {
        setCurrentInverterFactor(currentInverterFactor);
        return this;
    }

    public int getCurrentChargerFactor() {
        return currentChargerFactor;
    }

    public void setCurrentChargerFactor(final int currentChargerFactor) {
        this.currentChargerFactor = currentChargerFactor;
    }

    public FrameInfoDC withCurrentChargerFactor(final int currentChargerFactor) {
        setCurrentChargerFactor(currentChargerFactor);
        return this;
    }

    public int getPeriodFactor() {
        return periodFactor;
    }

    public void setPeriodFactor(final int periodFactor) {
        this.periodFactor = periodFactor;
    }

    public FrameInfoDC withPeriodFactor(final int periodFactor) {
        setPeriodFactor(periodFactor);
        return this;
    }

    @Override
    public Frame withData(byte[] data) {
        super.withData(data);
        setPhase(Phase.DC);

        setVoltageFactor(((data[7] & 0xff) << 8)
                + (data[6] & 0xff));

        setCurrentInverterFactor(
                ((data[10] & 0xff) << 16)
                + ((data[9] & 0xff) << 8)
                + (data[8] & 0xff));

        setCurrentChargerFactor(
                ((data[13] & 0xff) << 16)
                + ((data[12] & 0xff) << 8)
                + (data[11] & 0xff));
        setPeriodFactor(data[14] & 0xff);
        return this;
    }

    @Override
    public String toString() {
        return "FrameInfoDC{" + "voltage=" + voltageFactor + ", currentInverter=" + currentInverterFactor + ", currentCharger=" + currentChargerFactor + ", period=" + periodFactor + '}';
    }
}
