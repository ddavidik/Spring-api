package com.job.task.entity.endpoint;

import com.job.task.controller.RestEndpointController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class MonitoredEndpointAssembler implements RepresentationModelAssembler<MonitoredEndpoint, EntityModel<MonitoredEndpoint>> {

    @Override
    public EntityModel<MonitoredEndpoint> toModel(MonitoredEndpoint endpoint) {
        return EntityModel.of(endpoint,
                linkTo(methodOn(RestEndpointController.class).findEndpointById(endpoint.getId())).withSelfRel(),
                linkTo(methodOn(RestEndpointController.class).all()).withRel("endpoints"));
    }
}
