package com.job.task.service;

import com.job.task.entity.endpoint.MonitoredEndpoint;
import com.job.task.entity.result.MonitoringResult;
import com.job.task.repository.MonitoredEndpointRepository;
import com.job.task.repository.MonitoringResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class UrlService {

    private final RestTemplate restTemplate;
    private MonitoredEndpoint endpoint;

    private MonitoringResultRepository resultRepository;
    private MonitoredEndpointRepository endpointRepository;
    private static Logger logger = LoggerFactory.getLogger(UrlService.class);
    private ScheduledExecutorService executorService;

    public UrlService() {
        this.restTemplate = new RestTemplateBuilder().build();
    }

    public UrlService(MonitoredEndpoint endpoint, MonitoringResultRepository resultRepository, MonitoredEndpointRepository endpointRepository) {
        this.restTemplate = new RestTemplateBuilder().build();
        this.endpoint = endpoint;
        this.resultRepository = resultRepository;
        this.endpointRepository = endpointRepository;

        this.initExecutor();
    }

    public Runnable saveResult() {
        return () -> {
            MonitoringResult result = new MonitoringResult();
            try {
                ResponseEntity<String> responseEntity = this.restTemplate.getForEntity(this.endpoint.getUrl(), String.class);
                result.setHttpCode(responseEntity.getStatusCodeValue());
                result.setPayload(responseEntity.getBody());
            } catch (HttpClientErrorException e) {
                result.setHttpCode(e.getRawStatusCode());
                result.setPayload(e.getResponseBodyAsString());
            } catch (Exception e) {
                logger.error(e.getMessage());
                result.setError(e.getMessage() + ". Please update or delete this endpoint.");
            }
            result.setEndpoint(this.endpoint);
            MonitoredEndpoint endpoint = endpointRepository.findById(result.getEndpoint().getId()).get();
            endpoint.setLastChecked(new Date());
            endpointRepository.save(endpoint);
            resultRepository.save(result);

        };
    }

    public void endpointChanged(MonitoredEndpoint endpoint) {
        this.endpoint = endpoint;
        this.executorService.shutdown();
        this.initExecutor();
    }

    public void shutdownExecutor() {
        this.executorService.shutdown();
    }

    private void initExecutor() {
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.executorService.scheduleAtFixedRate(saveResult(), 0, endpoint.getMonitoredInterval(), TimeUnit.SECONDS);
    }

    public MonitoredEndpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(MonitoredEndpoint endpoint) {
        this.endpoint = endpoint;
    }
}
