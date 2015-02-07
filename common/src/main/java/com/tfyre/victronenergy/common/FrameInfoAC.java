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

    @Override
    public Frame withData(byte[] data) {
        super.withData(data);
        doPhase(data[5]);

        return this;
    }

}
