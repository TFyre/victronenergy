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
public class FrameInfoAC extends FrameInfo {

    private int phases;
    private int backfeedFactor;
    private int inverterFactor;
    private int voltageFactor;
    private int currentFactor;
    private int inverterVoltageFactor;
    private int inverterCurrentFactor;
    private int periodFactor;

    public int getPhases() {
        return phases;
    }

    public void setPhases(final int phases) {
        this.phases = phases;
    }

    public FrameInfoAC withPhases(final int phases) {
        setPhases(phases);
        return this;
    }

    public int getBackfeedFactor() {
        return backfeedFactor;
    }

    public void setBackfeedFactor(final int backfeedFactor) {
        this.backfeedFactor = backfeedFactor;
    }

    public FrameInfoAC withBackfeedFactor(final int backfeedFactor) {
        setBackfeedFactor(backfeedFactor);
        return this;
    }

    public int getInverterFactor() {
        return inverterFactor;
    }

    public void setInverterFactor(final int inverterFactor) {
        this.inverterFactor = inverterFactor;
    }

    public FrameInfoAC withInverterFactor(final int inverterFactor) {
        setInverterFactor(inverterFactor);
        return this;
    }

    public int getCurrentFactor() {
        return currentFactor;
    }

    public void setCurrentFactor(final int currentFactor) {
        this.currentFactor = currentFactor;
    }

    public FrameInfoAC withCurrentFactor(final int currentFactor) {
        setCurrentFactor(currentFactor);
        return this;
    }

    public int getInverterVoltageFactor() {
        return inverterVoltageFactor;
    }

    public void setInverterVoltageFactor(final int inverterVoltageFactor) {
        this.inverterVoltageFactor = inverterVoltageFactor;
    }

    public FrameInfoAC withInverterVoltageFactor(final int inverterVoltageFactor) {
        setInverterVoltageFactor(inverterVoltageFactor);
        return this;
    }

    public int getInverterCurrentFactor() {
        return inverterCurrentFactor;
    }

    public void setInverterCurrentFactor(final int inverterCurrentFactor) {
        this.inverterCurrentFactor = inverterCurrentFactor;
    }

    public FrameInfoAC withInverterCurrentFactor(final int inverterCurrentFactor) {
        setInverterCurrentFactor(inverterCurrentFactor);
        return this;
    }

    public int getPeriodFactor() {
        return periodFactor;
    }

    public void setPeriodFactor(final int periodFactor) {
        this.periodFactor = periodFactor;
    }

    public FrameInfoAC withPeriodFactor(final int periodFactor) {
        setPeriodFactor(periodFactor);
        return this;
    }

    private void doPhase(final byte data) {
        switch (data) {
            case 5:
                setPhase(Phase.L4);
                break;
            case 6:
                setPhase(Phase.L3);
                break;
            case 7:
                setPhase(Phase.L2);
                break;
            case 8:
                setPhase(Phase.L1);
                setPhases(1);
                break;
            case 9:
                setPhase(Phase.L1);
                setPhases(2);
                break;
            case 10:
                setPhase(Phase.L1);
                setPhases(3);
                break;
            case 11:
                setPhase(Phase.L1);
                setPhases(4);
                break;
        }

    }

    public int getVoltageFactor() {
        return voltageFactor;
    }

    public void setVoltageFactor(final int voltageFactor) {
        this.voltageFactor = voltageFactor;
    }

    public FrameInfoAC withVoltageFactor(final int voltageFactor) {
        setVoltageFactor(voltageFactor);
        return this;
    }

    @Override
    public Frame withData(byte[] data) {
        super.withData(data);
        setBackfeedFactor(data[1] & 0xff);
        setInverterFactor(data[2] & 0xff);
        doPhase(data[5]);
        setVoltageFactor(((data[7] & 0xff) << 8)
                + (data[6] & 0xff));
        setCurrentFactor(((data[9] & 0xff) << 8)
                + (data[8] & 0xff));
        setInverterVoltageFactor(((data[11] & 0xff) << 8)
                + (data[10] & 0xff));
        setInverterCurrentFactor(((data[13] & 0xff) << 8)
                + (data[12] & 0xff));
        setPeriodFactor((data[14] & 0xff));
        return this;
    }

    @Override
    public String toString() {
        return "FrameInfoAC{" + "phases=" + phases + ", backfeedFactor=" + backfeedFactor + ", inverterFactor=" + inverterFactor + ", voltageFactor=" + voltageFactor + ", currentFactor=" + currentFactor + ", inverterVoltageFactor=" + inverterVoltageFactor + ", inverterCurrentFactor=" + inverterCurrentFactor + ", periodFactor=" + periodFactor + '}';
    }

}
