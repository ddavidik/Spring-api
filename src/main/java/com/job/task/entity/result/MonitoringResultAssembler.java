package com.job.task.entity.result;

import com.job.task.controller.RestEndpointController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class MonitoringResultAssembler implements RepresentationModelAssembler<MonitoringResult, EntityModel<MonitoringResult>> {

    @Override
    public EntityModel<MonitoringResult> toModel(MonitoringResult result) {
        return EntityModel.of(result,
                linkTo(methodOn(RestEndpointController.class).findResultsForEndpoint(result.getEndpoint().getId())).withSelfRel(),
                linkTo(methodOn(RestEndpointController.class).findEndpointById(result.getEndpoint().getId())).withRel("endpoint"));
    }
}
