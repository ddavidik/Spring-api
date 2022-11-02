package com.job.task.entity.endpoint;

import com.job.task.entity.result.MonitoringResult;
import com.job.task.entity.user.User;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "monitored_endpoint")
public class MonitoredEndpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank
    @Column(name = "endpoint_name", unique = true)
    private String name;

    @NotBlank
    @URL
    @Column(name = "url", unique = true)
    private String url;

    // can also use java.sql.Timestamp for datetime values
    @Column(name = "created_at")
    private Date createdAt = new Date();

    @Column(name = "last_checked")
    private Date lastChecked;

    // in seconds
    @NotNull
    @Column(name = "monitored_interval")
    private Integer monitoredInterval;

    @ManyToOne
    @JoinColumn(name = "endpoint_owner")
    private User owner;

    @OneToMany(mappedBy = "endpoint", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<MonitoringResult> results;

    public MonitoredEndpoint() {
    }

    public MonitoredEndpoint(@NotBlank String name, @NotBlank @URL String url, @NotNull Integer monitoredInterval, User owner) {
        this.name = name;
        this.url = url;
        this.createdAt = new Date();
        this.monitoredInterval = monitoredInterval;
        this.owner = owner;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date created) {
        this.createdAt = created;
    }

    public Date getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(Date lastChecked) {
        this.lastChecked = lastChecked;
    }

    public Integer getMonitoredInterval() {
        return monitoredInterval;
    }

    public void setMonitoredInterval(int monitoredInterval) {
        this.monitoredInterval = monitoredInterval;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonitoredEndpoint that = (MonitoredEndpoint) o;
        return id == that.id && monitoredInterval == that.monitoredInterval && name.equals(that.name) && url.equals(that.url) && createdAt.equals(that.createdAt) && Objects.equals(lastChecked, that.lastChecked) && owner.equals(that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, url, createdAt, lastChecked, monitoredInterval, owner);
    }

    @Override
    public String toString() {
        return "MonitoredEndpoint{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", createdAt=" + createdAt +
                ", lastChecked=" + lastChecked +
                ", monitoredInterval=" + monitoredInterval +
                ", owner=" + owner +
                '}';
    }
}
