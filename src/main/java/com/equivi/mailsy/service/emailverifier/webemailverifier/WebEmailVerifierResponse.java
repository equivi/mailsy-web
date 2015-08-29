package com.equivi.mailsy.service.emailverifier.webemailverifier;


import com.equivi.mailsy.service.emailverifier.EmailVerifierResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WebEmailVerifierResponse implements EmailVerifierResponse{

    @JsonProperty("Address")
    private String address;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("StatusCode")
    private String statusCode;

    public WebEmailVerifierResponse() {}

    public WebEmailVerifierResponse(String address, String status, String statusCode) {
        this.address = address;
        this.status = status;
        this.statusCode = statusCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
