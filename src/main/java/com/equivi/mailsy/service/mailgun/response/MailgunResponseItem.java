package com.equivi.mailsy.service.mailgun.response;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class MailgunResponseItem implements Serializable {

    private static final long serialVersionUID = 7942930277508709593L;

    private String recipient;

    private String timestamp;

    private String event;

    @JsonProperty("client-info")
    private ClientInfo clientInfo;

    @JsonProperty("geolocation")
    private GeoLocation geoLocation;


    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    /**
     *
     * @param eventAPIType
     * @return true or false if this event type is equals EventAPIType(ACCEPTED,REJECTED,DELIVERED,etc) reference com.equivi.mailsy.service.mailgun.response.EventAPIType
     */
    public boolean isEventType(EventAPIType eventAPIType) {
        return this.event.equals(eventAPIType.getEventApiDescription());
    }
}
