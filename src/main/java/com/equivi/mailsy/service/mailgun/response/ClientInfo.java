package com.equivi.mailsy.service.mailgun.response;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class ClientInfo implements Serializable{

    private static final long serialVersionUID = -6738849510388208239L;

    @JsonProperty("client-name")
    private String clientName;

    @JsonProperty("client-os")
    private String clientOs;

    @JsonProperty("user-agent")
    private String userAgent;

    @JsonProperty("device-type")
    private String deviceType;

    @JsonProperty("client-type")
    private String clientType;


    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientOs() {
        return clientOs;
    }

    public void setClientOs(String clientOs) {
        this.clientOs = clientOs;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }
}
