/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tfyre.victronenergy.client;

/**
 *
 * @author acid
 */
public class Config {

    private String url;
    private int port;

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public Config withUrl(final String url) {
        setUrl(url);
        return this;
    }

    public int getPort() {
        return port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public Config withPort(final int port) {
        setPort(port);
        return this;
    }

}
