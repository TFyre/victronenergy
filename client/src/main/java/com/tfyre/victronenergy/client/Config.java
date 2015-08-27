/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tfyre.victronenergy.client;

import java.util.List;

/**
 *
 * @author acid
 */
public class Config {

    private String url;
    private int port;

    private final List<String> notifyDetail = new java.util.ArrayList<>();
    private final List<String> notifyNormal = new java.util.ArrayList<>();

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

    public List<String> getNotifyDetail() {
        return notifyDetail;
    }

    public void setNotifyDetail(final List<String> notifyDetail) {
        this.notifyDetail.clear();
        this.notifyDetail.addAll(notifyDetail);
    }

    public List<String> getNotifyNormal() {
        return notifyNormal;
    }

    public void setNotifyNormal(final List<String> notifyNormal) {
        this.notifyNormal.clear();
        this.notifyNormal.addAll(notifyNormal);
    }

}
