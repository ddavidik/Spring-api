package com.job.task.controller;

import com.job.task.entity.endpoint.MonitoredEndpointRequest;
import com.job.task.service.UrlService;
import com.job.task.entity.endpoint.MonitoredEndpoint;
import com.job.task.entity.endpoint.MonitoredEndpointAssembler;
import com.job.task.repository.MonitoredEndpointRepository;
import com.job.task.entity.result.MonitoringResult;
import com.job.task.entity.result.MonitoringResultAssembler;
import com.job.task.repository.MonitoringResultRepository;
import com.job.task.entity.user.User;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/rest/endpoints")
public class RestEndpointController {

    private final MonitoredEndpointRepository endpointRepository;
    private final MonitoringResultRepository resultRepository;
    private final MonitoredEndpointAssembler endpointAssembler;
    private final MonitoringResultAssembler resultAssembler;

    private Map<Integer, UrlService> urlServiceMap = new HashMap<>();

    public RestEndpointController(MonitoredEndpointRepository endpointRepository, MonitoredEndpointAssembler endpointAssembler,
                                  MonitoringResultRepository resultRepository, MonitoringResultAssembler resultAssembler) {
        this.endpointRepository = endpointRepository;
        this.resultRepository = resultRepository;
        this.endpointAssembler = endpointAssembler;
        this.resultAssembler = resultAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<MonitoredEndpoint>> all() {
        User currentUser = this.getCurrentUser();

        List<EntityModel<MonitoredEndpoint>> endpoints = endpointRepository.findAllByOwner(currentUser).stream()
                .map(endpointAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(endpoints,
                linkTo(methodOn(RestEndpointController.class).all()).withSelfRel());
    }

    @PostAuthorize("returnObject.content.owner.accessToken == authentication.principal.accessToken")
    @GetMapping("/{id}")
    public EntityModel<MonitoredEndpoint> findEndpointById(@PathVariable Integer id) {
        Optional<MonitoredEndpoint> _endpoint = endpointRepository.findById(id);

        if (_endpoint.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        MonitoredEndpoint endpoint = _endpoint.get();

        return endpointAssembler.toModel(endpoint);
    }

    @GetMapping("/{id}/results")
    public CollectionModel<EntityModel<MonitoringResult>> findResultsForEndpoint(@PathVariable Integer id) {
        Optional<MonitoredEndpoint> _endpoint = endpointRepository.findById(id);

        if (_endpoint.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        MonitoredEndpoint endpoint = _endpoint.get();

        User currentUser = this.getCurrentUser();

        if (!currentUser.getAccessToken().equals(endpoint.getOwner().getAccessToken())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        List<EntityModel<MonitoringResult>> results = resultRepository.findTop10ByEndpointOrderByIdDesc(endpoint).stream()
                .map(resultAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(results);
    }

    @PostMapping
    public ResponseEntity<EntityModel<MonitoredEndpoint>> createNewEndpoint(@Valid @RequestBody MonitoredEndpointRequest endpointRequest) {

        User currentUser = this.getCurrentUser();
        MonitoredEndpoint endpoint = new MonitoredEndpoint(endpointRequest.getName(), endpointRequest.getUrl(), endpointRequest.getInterval(), currentUser);

        EntityModel<MonitoredEndpoint> endpointModel = endpointAssembler.toModel(endpointRepository.save(endpoint));
        this.urlServiceMap.put(endpoint.getId(), new UrlService(endpoint, resultRepository, endpointRepository));
        return ResponseEntity.created(endpointModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(endpointModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEndpoint(@PathVariable Integer id) {
        Optional<MonitoredEndpoint> _endpoint = endpointRepository.findById(id);

        User currentUser = this.getCurrentUser();

        if (_endpoint.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MonitoredEndpoint endpoint = _endpoint.get();

        if (!currentUser.getAccessToken().equals(endpoint.getOwner().getAccessToken())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        this.urlServiceMap.get(endpoint.getId()).shutdownExecutor();
        this.urlServiceMap.remove(endpoint.getId());

        endpointRepository.delete(endpoint);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("#endpoint.owner.accessToken == authentication.principal.accessToken")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEndpoint(@PathVariable Integer id, @Valid @RequestBody MonitoredEndpoint endpoint) {
        Optional<MonitoredEndpoint> _endpoint = endpointRepository.findById(id);

        if(_endpoint.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (this.urlServiceMap.get(endpoint.getId()) != null) {
            this.urlServiceMap.get(endpoint.getId()).endpointChanged(endpoint);
        }
        return ResponseEntity.ok(endpointAssembler.toModel(endpointRepository.save(endpoint)));
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
