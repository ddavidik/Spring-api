package com.job.task;

import com.job.task.controller.EndpointController;
import com.job.task.controller.RestEndpointController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class TaskApplicationTests {

	@Autowired
	private EndpointController endpointController;

	@Autowired
	private RestEndpointController restEndpointController;

	@Test
	void contextLoads() {
		assertThat(endpointController).isNotNull();
		assertThat(restEndpointController).isNotNull();
	}

}
