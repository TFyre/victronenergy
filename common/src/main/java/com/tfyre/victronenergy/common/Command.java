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
public enum Command {

    SOFTWARE_PART_1("Software Part 1", (byte) 0x05),
    SOFTWARE_PART_2("Software Part 2", (byte) 0x06),
    DEVICE_STATE("DEVICE_STATE", (byte) 0x0e),
    RAM_VAR_INFO("RAM Var Info", (byte) 0x36),
    RESPONSE_SOFTWARE_PART_1("Software Part 1", (byte) 0x82),
    RESPONSE_SOFTWARE_PART_2("Software Part 2", (byte) 0x83),
    RESPONSE_RAM_VAR_INFO1("RAM Var Info1", (byte) 0x8e),
    RESPONSE_RAM_VAR_INFO2("RAM Var Info2", (byte) 0x8f),
    RESPONSE_DEVICE_STATE("DEVICE_STATE", (byte) 0x94)
    ;

    private final String name;
    private final byte command;

    private Command(final String name, final byte command) {
        this.name = name;
        this.command = command;
    }

    public static Command fromCommand(final byte command) {
        for (final Command _command : values()) {
            if (_command.getCommand() == command) {
                return _command;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public byte getCommand() {
        return command;
    }

}
