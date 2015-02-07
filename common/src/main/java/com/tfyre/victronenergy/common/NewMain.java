/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tfyre.victronenergy.common;

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
    private boolean info1;
    private boolean hasSettings;
    private RAMVariable ramVariable;
    private double dcVScale;
    private double dcIScale;
    private double dcITime;

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

    private void handleFrameCommandVariable(final FrameCommand frame) {
        switch (ramVariable) {
            case UBAT:
                dcVScale = frame.getFraction();
                System.out.println("dcVScale: " + dcVScale);
                ramVariable = RAMVariable.IBAT;
                socket.addWriterFrame(getFrameCommand(Command.RAM_VAR_INFO)
                        .withInfo1(ramVariable.getId()));
                break;
            case IBAT:
                dcIScale = frame.getFraction();
                System.out.println("dcIScale: " + dcIScale);
                ramVariable = RAMVariable.ITIME;
                socket.addWriterFrame(getFrameCommand(Command.RAM_VAR_INFO)
                        .withInfo1(ramVariable.getId()));
                break;
            case ITIME:
                dcITime = frame.getFraction();
                System.out.println("dcITime: " + dcITime);
                hasSettings = true;
                break;
            default:
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
                ramVariable = RAMVariable.UBAT;
                socket.addWriterFrame(getFrameCommand(Command.RAM_VAR_INFO)
                        .withInfo1(ramVariable.getId()));
                break;
            case RESPONSE_RAM_VAR_INFO1:
                handleFrameCommandVariable(frame);
                break;
        }
    }

    private void handleFrame(final Frame frame) {
        System.out.println("frame: " + frame.getClass() + " " + frame.toString());
        if (frame instanceof FrameAddress) {
            handleFrameAddress((FrameAddress) frame);
        } else if (frame instanceof FrameCommand) {
            handleFrameCommand((FrameCommand) frame);
        } else if (frame instanceof FrameInfoDC) {
            final FrameInfoDC frameDC = (FrameInfoDC) frame;
            System.out.println("DC Voltage: " + frameDC.getVoltageFactor() * dcVScale);
            System.out.println("DC Inverter: " + frameDC.getCurrentInverterFactor() * dcIScale);
            System.out.println("DC Charger: " + frameDC.getCurrentChargerFactor() * dcIScale);
            System.out.println("DC Freq: " + (10 / ((frameDC.getPeriodFactor() + 256) * dcITime)));
        }
    }

    private boolean processQueue() {
        //if (queue.isEmpty()) {
        //    return false;
        //}
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
        info1 = !info1;
        socket.addWriterFrame(getFrameInfo(info1 ? 0 : 1));
        //socket.addWriterFrame(getFrameInfo(5));
        //socket.addWriterFrame(getFrameInfo(2));
        //socket.addWriterFrame(getFrameInfo(3));
        //socket.addWriterFrame(getFrameInfo(4));
        //socket.addWriterFrame(getFrameInfo(5));
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
