package com.job.task.repository;

import com.job.task.entity.endpoint.MonitoredEndpoint;
import com.job.task.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MonitoredEndpointRepository extends JpaRepository<MonitoredEndpoint, Integer> {

    List<MonitoredEndpoint> findAllByOwner(User owner);
    Optional<MonitoredEndpoint> findByOwner(User owner);
    Optional<MonitoredEndpoint> findByName(String name);
    Optional<MonitoredEndpoint> findByUrl(String url);

}
