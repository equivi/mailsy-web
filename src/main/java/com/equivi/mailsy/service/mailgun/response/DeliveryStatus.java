package com.equivi.mailsy.service.mailgun.response;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class DeliveryStatus implements Serializable{
    private static final long serialVersionUID = -5467017915531033321L;

    private String message;

    private String code;

    private String description;

    @JsonProperty(value = "session-seconds")
    private String sessionSeconds;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSessionSeconds() {
        return sessionSeconds;
    }

    public void setSessionSeconds(String sessionSeconds) {
        this.sessionSeconds = sessionSeconds;
    }
}
