package com.job.task.controller;

import com.job.task.service.UrlService;
import com.job.task.entity.endpoint.MonitoredEndpoint;
import com.job.task.repository.MonitoredEndpointRepository;
import com.job.task.entity.result.MonitoringResult;
import com.job.task.repository.MonitoringResultRepository;
import com.job.task.entity.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/endpoints")
public class EndpointController {

    private final MonitoredEndpointRepository endpointRepository;
    private final MonitoringResultRepository resultRepository;

    private Map<Integer, UrlService> urlServiceMap = new HashMap<>();

    public EndpointController(MonitoredEndpointRepository endpointRepository, MonitoringResultRepository resultRepository) {
        this.endpointRepository = endpointRepository;
        this.resultRepository = resultRepository;
    }

    @GetMapping
    public ModelAndView all() {
        List<MonitoredEndpoint> endpoints = endpointRepository.findAllByOwner(this.getAuthenticatedUser());

        return new ModelAndView("endpoints","endpoints", endpoints);
    }

    @GetMapping("/{id}/results")
    public ModelAndView findResultsForEndpoint(@PathVariable Integer id) {
        MonitoredEndpoint endpoint = checkEndpointById(id);

        List<MonitoringResult> results = resultRepository.findTop10ByEndpointOrderByIdDesc(endpoint);
        return new ModelAndView("results", "results", results);
    }

    @GetMapping("/{id}")
    public ModelAndView findEndpointById(@PathVariable Integer id) {
        MonitoredEndpoint endpoint = this.checkEndpointById(id);

        return new ModelAndView("endpoint", "endpoint", endpoint);
    }

    @GetMapping("/create")
    public ModelAndView showCreateEndpointForm() {
        return new ModelAndView("create", "endpoint", new MonitoredEndpoint());
    }

    @PostMapping
    public String saveEndpoint(@Valid @ModelAttribute("endpoint") MonitoredEndpoint endpoint, BindingResult result, Model model) {

        if (endpointRepository.findByName(endpoint.getName()).isPresent() || endpointRepository.findByUrl(endpoint.getUrl()).isPresent()) {
            model.addAttribute("nameUrlError", true);
            return "create";
        }

        if (result.hasErrors()) {
            return "create";
        }

        MonitoredEndpoint newEndpoint = new MonitoredEndpoint(endpoint.getName(), endpoint.getUrl(), endpoint.getMonitoredInterval(), this.getAuthenticatedUser());

        endpointRepository.save(newEndpoint);

        this.urlServiceMap.put(newEndpoint.getId(), new UrlService(newEndpoint, resultRepository, endpointRepository));
        return "redirect:/endpoints";
    }

    @DeleteMapping("/{id}")
    public String deleteEndpoint(@PathVariable Integer id) {

        Optional<MonitoredEndpoint> _endpoint = endpointRepository.findById(id);

        if (_endpoint.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        MonitoredEndpoint endpoint = _endpoint.get();

        this.urlServiceMap.get(endpoint.getId()).shutdownExecutor();
        this.urlServiceMap.remove(endpoint.getId());

        endpointRepository.delete(endpoint);
        return "redirect:/endpoints";
    }

    @PutMapping("/{id}")
    public String updateEndpoint(@PathVariable Integer id, @Valid @ModelAttribute("endpoint") MonitoredEndpoint endpointModel, BindingResult result, Model model) {

        Optional<MonitoredEndpoint> _endpoint = endpointRepository.findById(id);

        if (_endpoint.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        MonitoredEndpoint updatedEndpoint = _endpoint.get();

        if ((endpointRepository.findByName(endpointModel.getName()).isPresent() || endpointRepository.findByUrl(endpointModel.getUrl()).isPresent())
                && (!updatedEndpoint.getName().equals(endpointModel.getName()) || !updatedEndpoint.getUrl().equals(endpointModel.getUrl()))) {
            endpointModel.setOwner(updatedEndpoint.getOwner());
            model.addAttribute("nameUrlError", true);
            return "endpoint";
        }

        if (result.hasErrors()) {
            endpointModel.setOwner(updatedEndpoint.getOwner());
            return "endpoint";
        }

        if (!this.getAuthenticatedUser().getAccessToken().equals(updatedEndpoint.getOwner().getAccessToken())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        updatedEndpoint.setName(endpointModel.getName());
        updatedEndpoint.setUrl(endpointModel.getUrl());
        updatedEndpoint.setMonitoredInterval(endpointModel.getMonitoredInterval());
        endpointRepository.save(updatedEndpoint);
        this.urlServiceMap.get(id).endpointChanged(updatedEndpoint);

        return "redirect:/endpoints";

    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private MonitoredEndpoint checkEndpointById(Integer id) {
        Optional<MonitoredEndpoint> _endpoint = endpointRepository.findById(id);

        if (_endpoint.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        MonitoredEndpoint endpoint = _endpoint.get();

        if (!endpoint.getOwner().getAccessToken().equals(this.getAuthenticatedUser().getAccessToken())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return endpoint;
    }
}
