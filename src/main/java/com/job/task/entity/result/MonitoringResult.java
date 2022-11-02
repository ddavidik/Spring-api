package com.job.task.entity.result;

import com.job.task.entity.endpoint.MonitoredEndpoint;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "monitoring_result")
public class MonitoringResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "checked_at")
    private Date checkedAt;

    @Column(name = "http_code")
    private Integer httpCode;

    @Column(name = "payload")
    @Type(type = "text")
    private String payload;

    @Column(name = "http_error")
    private String error;

    @ManyToOne
    @JoinColumn(name = "monitored_endpoint")
    private MonitoredEndpoint endpoint;

    public MonitoringResult() {
        this.checkedAt = new Date();
    }

    public MonitoringResult(Integer httpCode, String payload, MonitoredEndpoint endpoint) {
        this.checkedAt = new Date();
        this.httpCode = httpCode;
        this.payload = payload;
        this.endpoint = endpoint;
    }

    public Integer getId() {
        return id;
    }

    public Date getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(Date checked) {
        this.checkedAt = checked;
    }

    public Integer getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public MonitoredEndpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(MonitoredEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonitoringResult that = (MonitoringResult) o;
        return id == that.id && httpCode == that.httpCode && Objects.equals(checkedAt, that.checkedAt) && payload.equals(that.payload) && endpoint.equals(that.endpoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, checkedAt, httpCode, payload, endpoint);
    }

    @Override
    public String toString() {
        return "MonitoringResult{" +
                "id=" + id +
                ", checkedAt=" + checkedAt +
                ", httpCode=" + httpCode +
                ", payload='" + payload + '\'' +
                ", endpoint=" + endpoint +
                ", error=" + error +
                '}';
    }
}
