package com.equivi.mailsy.service.mailgun.response;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;


public class UnsubscribeResponseItems implements Serializable{
    private static final long serialVersionUID = 4172834621318878304L;

    private String id;

    private String address;

    private String tag;

    @JsonProperty(value = "created_at")
    private String createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
