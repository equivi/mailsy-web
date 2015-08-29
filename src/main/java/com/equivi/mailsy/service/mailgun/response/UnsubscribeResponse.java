package com.equivi.mailsy.service.mailgun.response;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UnsubscribeResponse {

    @JsonProperty(value = "total_count")
    private Integer total;

    @JsonProperty(value = "items")
    private List<UnsubscribeResponseItems> items;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<UnsubscribeResponseItems> getItems() {
        return items;
    }

    public void setItems(List<UnsubscribeResponseItems> items) {
        this.items = items;
    }
}
