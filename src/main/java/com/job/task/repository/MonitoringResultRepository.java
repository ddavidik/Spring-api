package com.job.task.repository;

import com.job.task.entity.endpoint.MonitoredEndpoint;
import com.job.task.entity.result.MonitoringResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonitoringResultRepository extends JpaRepository<MonitoringResult, Integer> {

    List<MonitoringResult> findTop10ByEndpointOrderByIdDesc(MonitoredEndpoint endpoint);

}
