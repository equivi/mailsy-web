package com.equivi.mailsy.service.emailverifier.byteplant;


import com.equivi.mailsy.service.emailverifier.EmailVerifierResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BytePlantEmailVerifierResponse implements EmailVerifierResponse {

    @JsonProperty("status")
    private String statusCode;

    @JsonProperty("info")
    private String info;

    @JsonProperty("details")
    private String details;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
