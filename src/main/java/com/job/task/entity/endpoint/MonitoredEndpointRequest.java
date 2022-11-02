package com.job.task.entity.endpoint;

public class MonitoredEndpointRequest {

    private String name;
    private String url;
    private Integer interval;

    public MonitoredEndpointRequest() {
    }

    public MonitoredEndpointRequest(String name, String url, Integer interval) {
        this.name = name;
        this.url = url;
        this.interval = interval;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }
}
