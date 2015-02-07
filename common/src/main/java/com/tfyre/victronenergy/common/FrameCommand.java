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
public class FrameCommand extends Frame {

    private Command command;
    private int info1;
    private int info2;

    public Command getCommand() {
        return command;
    }

    public void setCommand(final Command command) {
        this.command = command;
    }

    public FrameCommand withCommand(final Command command) {
        setCommand(command);
        return this;
    }

    public int getInfo1() {
        return info1;
    }

    public void setInfo1(int info1) {
        this.info1 = info1;
    }

    public FrameCommand withInfo1(final int info1) {
        setInfo1(info1);
        return this;
    }

    public int getInfo2() {
        return info2;
    }

    public void setInfo2(int info2) {
        this.info2 = info2;
    }

    public FrameCommand withInfo2(final int info2) {
        setInfo2(info2);
        return this;
    }

    public double getFraction() {
        final double result = (getInfo2() << 8) + getInfo1();
        if (result > 0x4000) {
            return (double) Math.round(1000000 / (0x8000 - result)) / 1000000;
        }
        return result;
    }

    public int getOffset() {
        final byte[] _data = getData();
        if (_data.length < 8) {
            return 0;
        }
        if (_data[5] != Command.RESPONSE_RAM_VAR_INFO2.getCommand()) {
            return 0;
        }
        return ((_data[7] & 0xff) << 8) + (_data[6] & 0xff); 
    }
    
    @Override
    public byte[] getRequest() {
        return new byte[]{(byte) 'W', (byte) command.getCommand(), (byte) info1};
    }

    @Override
    public Frame withData(byte[] data) {
        super.withData(data);
        setCommand(Command.fromCommand(data[2]));
        setInfo1(data[3] & 0xff);
        setInfo2(data[4] & 0xff);
        return this;
    }

    private String getStateDescription() {
        switch (info1) {
            case 0:
                return "Down";
            case 1:
                return "Startup";
            case 2:
                return "Off";
            case 3:
                return "Device in Slave Mode";
            case 4:
                return "Invert Full";
            case 5:
                return "Invert Half";
            case 6:
                return "Invert AES";
            case 7:
                return "Power Assist";
            case 8:
                return "Bypass";
            case 9:
                return "Charge";
            default:
                return "Unknown";
        }
    }

    private String getSubStateDescription() {
        if (info1 != 9) {
            return "invalid";
        }

        switch (info2) {
            case 0:
                return "Initializing";
            case 1:
                return "Bulk";
            case 2:
                return "Absorption";
            case 3:
                return "Float";
            case 4:
                return "Storage";
            case 5:
                return "Repeated Absorbtion";
            case 6:
                return "Forced Absorbtion";
            case 7:
                return "Equalise";
            case 8:
                return "Bulk Stopped";
            default:
                return "Unknown";
        }
    }

    @Override
    public String toString() {
        final String extra;
        switch (command) {
            case DEVICE_STATE:
            case RESPONSE_DEVICE_STATE:
                extra = ", info1s=" + getStateDescription() + ", info2s=" + getSubStateDescription();
                break;
            default:
                extra = "";
                break;
        }
        return "FrameCommand{" + "command=" + command + ", info1=" + info1 + ", info2=" + info2 + extra + '}';
    }

}
