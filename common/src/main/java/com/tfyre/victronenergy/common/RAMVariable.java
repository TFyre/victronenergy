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
public enum RAMVariable {

    UMAIN("Main Voltage Scale", 0x00),
    IMAIN("Main Current Scale", 0x01),
    UINVERTER("Inverter Voltage Scale", 0x02),
    IINVERTER("Inverter Current Scale", 0x03),
    UBAT("Battery Voltage Scale", 0x04),
    IBAT("Battery Current Scale", 0x05),
    ITIME("Inverter Period Time", 0x07),
    MTIME("Mains Period Time", 0x08);

    private final String name;
    private final int id;

    private RAMVariable(final String name, final int id) {
        this.name = name;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
