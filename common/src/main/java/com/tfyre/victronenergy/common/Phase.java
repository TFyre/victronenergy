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
public enum Phase {

    DC("dc"),
    L1("l1"),
    L2("l2"),
    L3("l3"),
    L4("l4");

    private final String name;

    private Phase(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
