/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tfyre.victronenergy.client;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author acid
 */
public class NewMain implements CallbackInterface, HttpHandler {

    private static final Logger LOG = Logger.getLogger(NewMain.class.getName());
    private static final String FRANCOIS = "27829403901";
    private static final String DANELLE = "27823066444";
    private final Queue<Frame> queue = new java.util.concurrent.LinkedBlockingQueue<>();
    private final Socket socket;
    private final HttpServer httpServer;
    private long lastInfo;
    private int nextW;
    private int info;
    private boolean hasSettings;
    private RAMVariable ramVariable;
    private double lastVoltage;

    private final DeviceSettings deviceSettings = new DeviceSettings();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        LOG.info("starting");
        final NewMain _main = new NewMain();
        _main.run();
    }

    public NewMain() throws IOException {
        lastVoltage = -200;
        socket = new Socket(this);
        httpServer = HttpServer.create(new InetSocketAddress(8000), 0);
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
        deviceSettings.setFrameInfoDC(frame);
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer(deviceSettings.getFrameInfoDCData());
        }
    }

    private void sendWhatsApp(final String to, final String msg) {
        try {
            final URL url = new URL(String.format("http://10.1.0.20:8000/send?to=%s&msg=%s", to, URLEncoder.encode(msg, "UTF-8")));
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine(url.toString());
            }
            final StringBuilder sb = new StringBuilder();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    sb.append(inputLine);
                    sb.append("\n");
                }
            }
            LOG.info(String.format("WA Message: %s - %s - %s", to, sb.toString().trim(), msg));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }

    }

    private void handleFrameInfoAC(final FrameInfoAC frame) {
        if (lastVoltage < 0) {
            lastVoltage = deviceSettings.getScaledValue1(RAMVariable.UMAIN, frame.getVoltageFactor());
            final String msg = String.format("Voltage: %.2f", lastVoltage);
            sendWhatsApp(FRANCOIS, msg);
        }
        deviceSettings.setFrameInfoAC(frame);
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer(deviceSettings.getFrameInfoACData());
        }
    }

    private void handleFrameLED(final FrameLED frame) {
        deviceSettings.setFrameLED(frame);
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer(deviceSettings.getFrameLEDData());
        }
    }

    private void handleFrame(final Frame frame) {
        if (frame instanceof FrameVersion) {
            if (LOG.isLoggable(Level.FINER)) {
                LOG.finer(String.format("%s - %s", frame.getClass().getSimpleName(), frame.toString()));
            }
        } else {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine(String.format("%s - %s", frame.getClass().getSimpleName(), frame.toString()));
            }
        }
        if (frame instanceof FrameAddress) {
            handleFrameAddress((FrameAddress) frame);
        } else if (frame instanceof FrameCommand) {
            handleFrameCommand((FrameCommand) frame);
        } else if (frame instanceof FrameInfoDC) {
            handleFrameInfoDC((FrameInfoDC) frame);
        } else if (frame instanceof FrameInfoAC) {
            handleFrameInfoAC((FrameInfoAC) frame);
        } else if (frame instanceof FrameLED) {
            handleFrameLED((FrameLED) frame);
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
        httpServer.createContext("/", this);
        httpServer.setExecutor(null);
        httpServer.start();
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

    private Map<String, String> getParameters(final String query) {
        final Map<String, String> result = new HashMap<>();
        for (final String param : query.split("&")) {
            try {
                final String[] pair = param.split("=");
                if (pair.length != 2) {
                    LOG.info(String.format("Invalid Parameter: %s", java.util.Arrays.toString(pair)));
                    continue;
                }
                result.put(pair[0], URLDecoder.decode(pair[1], "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return result;
    }

    private String handleStatus(final String query) {
        final Map<String, String> params = getParameters(query);
        if (!params.containsKey("type")) {
            return "need type";
        }

        final String type = params.get("type");

        if ("voltage".equalsIgnoreCase(type)) {
            return String.format("Voltage: %.2f", lastVoltage);
        } else if ("dc".equalsIgnoreCase(type)) {
            return deviceSettings.getFrameInfoDCData();
        } else if ("ac".equalsIgnoreCase(type)) {
            return deviceSettings.getFrameInfoACData();
        } else if ("led".equalsIgnoreCase(type)) {
            return deviceSettings.getFrameLEDData();
        } else {
            return "invalid type";
        }
    }

    @Override
    public void handle(final HttpExchange he) throws IOException {
        final String path = he.getRequestURI().getPath();
        final String query = he.getRequestURI().getQuery();
        LOG.info(String.format("%s - %s - %s", he.getRemoteAddress(), path, query));
        final String result;
        if ("/status".equalsIgnoreCase(path)) {
            result = handleStatus(query);
        } else {
            result = "fail";

        }
        he.sendResponseHeaders(200, result.length());
        try (final OutputStream os = he.getResponseBody()) {
            os.write(result.getBytes());
        }
    }
}
