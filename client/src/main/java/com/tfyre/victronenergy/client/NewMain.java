/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tfyre.victronenergy.client;

import com.tfyre.victronenergy.common.CallbackInterface;
import com.tfyre.victronenergy.common.Command;
import com.tfyre.victronenergy.common.DeviceSettings;
import com.tfyre.victronenergy.common.Frame;
import com.tfyre.victronenergy.common.FrameAddress;
import com.tfyre.victronenergy.common.FrameCommand;
import com.tfyre.victronenergy.common.FrameInfo;
import com.tfyre.victronenergy.common.FrameInfoAC;
import com.tfyre.victronenergy.common.FrameInfoDC;
import com.tfyre.victronenergy.common.FrameLED;
import com.tfyre.victronenergy.common.FrameReset;
import com.tfyre.victronenergy.common.FrameVersion;
import com.tfyre.victronenergy.common.RAMVariable;
import com.tfyre.victronenergy.common.Socket;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author acid
 */
public class NewMain implements CallbackInterface {

    private static final Logger LOG = Logger.getLogger(NewMain.class.getName());
    private final Queue<Frame> queue = new java.util.concurrent.LinkedBlockingQueue<>();
    private final Socket socket;
    private long lastInfo;
    private int nextW;
    private int info;
    private boolean hasSettings;
    private RAMVariable ramVariable;

    private final DeviceSettings deviceSettings = new DeviceSettings();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LOG.info("starting");
        final NewMain _main = new NewMain();
        _main.run();
    }

    public NewMain() {
        socket = new Socket(this);
    }

    private void handleFrameAddress(final FrameAddress frame) {
        nextW = 1;
        socket.addWriterFrame(getFrameCommand(Command.SOFTWARE_PART_1));
    }

    private void getVariable(final RAMVariable ramVariable) {
        this.ramVariable = ramVariable;
        socket.addWriterFrame(getFrameCommand(Command.RAM_VAR_INFO)
                .withInfo1(ramVariable.getId()));
    }

    private void handleFrameCommandVariable(final FrameCommand frame) {
        deviceSettings.setMap(ramVariable, frame);
        switch (ramVariable) {
            case UBAT:
                getVariable(RAMVariable.IBAT);
                break;
            case IBAT:
                getVariable(RAMVariable.ITIME);
                break;
            case ITIME:
                getVariable(RAMVariable.UMAIN);
                break;
            case UMAIN:
                getVariable(RAMVariable.IMAIN);
                break;
            case IMAIN:
                getVariable(RAMVariable.MTIME);
                break;
            case MTIME:
                getVariable(RAMVariable.UINVERTER);
                break;
            case UINVERTER:
                getVariable(RAMVariable.IINVERTER);
                break;
            default:
                hasSettings = true;
                break;
        }
    }

    private void handleFrameCommand(final FrameCommand frame) {
        switch (frame.getCommand()) {
            case RESPONSE_SOFTWARE_PART_1:
                socket.addWriterFrame(getFrameCommand(Command.SOFTWARE_PART_2));
                break;
            case RESPONSE_SOFTWARE_PART_2:
                socket.addWriterFrame(getFrameCommand(Command.DEVICE_STATE));
                break;
            case RESPONSE_DEVICE_STATE:
                getVariable(RAMVariable.UBAT);
                break;
            case RESPONSE_RAM_VAR_INFO1:
                handleFrameCommandVariable(frame);
                break;
        }
    }

    private void handleFrameInfoDC(final FrameInfoDC frame) {
        System.out.println("DC Voltage: " + deviceSettings.getScaledValue1(RAMVariable.UBAT, frame.getVoltageFactor()));
        System.out.println("DC Inverter: " + deviceSettings.getScaledValue1(RAMVariable.IBAT, frame.getCurrentInverterFactor()));
        System.out.println("DC Charger: " + deviceSettings.getScaledValue1(RAMVariable.IBAT, frame.getCurrentChargerFactor()));
        System.out.println("DC Freq: " + deviceSettings.getScaledValue2(RAMVariable.ITIME, frame.getPeriodFactor()));
    }

    private void handleFrameInfoAC(final FrameInfoAC frame) {
        System.out.println("Input Voltage: " + deviceSettings.getScaledValue1(RAMVariable.UMAIN, frame.getVoltageFactor()));
        System.out.println("Input Current: " + deviceSettings.getScaledValue3(RAMVariable.IMAIN, frame.getCurrentFactor(), frame.getBackfeedFactor()));
        System.out.println("Inverter Voltage: " + deviceSettings.getScaledValue1(RAMVariable.UINVERTER, frame.getInverterVoltageFactor()));
        System.out.println("Inverter Current: " + deviceSettings.getScaledValue3(RAMVariable.IINVERTER, frame.getInverterCurrentFactor(), frame.getInverterFactor()));
        System.out.println("Input Freq: " + deviceSettings.getScaledValue2(RAMVariable.MTIME, frame.getPeriodFactor()));
    }

    private void handleFrame(final Frame frame) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine(String.format("%s - %s", frame.getClass().getSimpleName(), frame.toString()));
        }
        if (frame instanceof FrameAddress) {
            handleFrameAddress((FrameAddress) frame);
        } else if (frame instanceof FrameCommand) {
            handleFrameCommand((FrameCommand) frame);
        } else if (frame instanceof FrameInfoDC) {
            handleFrameInfoDC((FrameInfoDC) frame);
        } else if (frame instanceof FrameInfoAC) {
            handleFrameInfoAC((FrameInfoAC) frame);
        }
    }

    private boolean processQueue() {
        final Frame frame = queue.poll();
        if (frame == null) {
            return false;
        }
        handleFrame(frame);
        return true;
    }

    private FrameReset getFrameReset() {
        return new FrameReset();
    }

    private FrameVersion getFrameVersion() {
        return new FrameVersion();
    }

    private FrameAddress getFrameAddress(final int id) {
        return new FrameAddress()
                .withId(id);
    }

    private FrameInfo getFrameInfo(final int id) {
        return new FrameInfo()
                .withType(id);
    }

    private FrameLED getFrameLED() {
        return new FrameLED();
    }

    private FrameCommand getFrameCommand(final Command command) {
        return new FrameCommand()
                .withCommand(command);
    }

    private void doSettings() {
        if (nextW == 0) {
            socket.addWriterFrame(getFrameAddress(0));
        }
    }

    private void doInfo() {
        if (!hasSettings) {
            doSettings();
            return;
        }
        if (lastInfo > (System.currentTimeMillis() - (2 * 1000))) {
            return;
        }
        switch (info) {
            case 0:
                socket.addWriterFrame(getFrameInfo(0));
                break;
            case 1:
                socket.addWriterFrame(getFrameInfo(1));
                break;
            case 2:
                socket.addWriterFrame(getFrameLED());
                break;
        }
        info++;
        if (info > 2) {
            info = 0;
        }
        lastInfo = System.currentTimeMillis();
    }

    private void run() {
        socket.getThread().start();
        while (true) {
            try {
                synchronized (queue) {
                    queue.wait();
                }
                while (processQueue()) {
                }
                doInfo();
            } catch (InterruptedException ex) {
                Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @Override
    public void processFrame(final Frame frame) {
        synchronized (queue) {
            queue.add(frame);
            queue.notifyAll();
        }
    }

}
