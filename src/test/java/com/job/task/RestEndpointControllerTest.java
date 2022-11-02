package com.job.task;

import com.job.task.entity.endpoint.MonitoredEndpoint;
import com.job.task.entity.result.MonitoringResult;
import com.job.task.entity.user.User;
import com.job.task.repository.MonitoredEndpointRepository;
import com.job.task.repository.MonitoringResultRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RestEndpointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private MonitoredEndpointRepository endpointRepository;

    @MockBean
    private MonitoringResultRepository resultRepository;

    private User user;
    private MonitoredEndpoint endpoint;
    private List<MonitoredEndpoint> endpoints;

    @BeforeAll
    public void setup() {
        user = new User("Applifting", "info@applifting.cz", "93f39e2f-80de-4033-99ee-249d92736a25");
        endpoint = new MonitoredEndpoint("Test", "https://test.com", 10, user);
        endpoints = Arrays.asList(endpoint);
    }

    @BeforeEach
    public void initAuth() {
        final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterAll
    public void teardown() {
        user = null;
        endpoint = null;
        endpoints = null;
    }

    @Test
    public void shouldReturnOneEndpoint() throws Exception {
        when(endpointRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(endpoint));

        mockMvc.perform(get("/rest/endpoints/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/rest/endpoints/{id}"));
    }

    @Test
    public void shouldReturnAllEndpoints() throws Exception {
        when(endpointRepository.findAllByOwner(user)).thenReturn(endpoints);

        mockMvc.perform(get("/rest/endpoints"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.monitoredEndpointList[0].name").value("Test"))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/rest/endpoints"));
    }

    @Test
    public void shouldReturnResults() throws Exception {

        List<MonitoringResult> results = Arrays.asList(new MonitoringResult(200, "whatever", endpoint));

        when(endpointRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(endpoint));
        when(resultRepository.findTop10ByEndpointOrderByIdDesc(endpoint)).thenReturn(results);

        mockMvc.perform(get("/rest/endpoints/1/results"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.monitoringResultList[0].payload").value("whatever"))
                .andExpect(jsonPath("$._embedded.monitoringResultList[0]._links.self.href").value("http://localhost/rest/endpoints/{id}/results"))
                .andExpect(jsonPath("$._embedded.monitoringResultList[0]._links.endpoint.href").value("http://localhost/rest/endpoints/{id}"));
    }

    @Test
    public void shouldCreateNewEndpoint() throws Exception {
        when(endpointRepository.save(endpoint)).thenReturn(endpoint);

        mockMvc.perform(post("/rest/endpoints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(endpoint)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test"));
    }

    @Test
    public void shouldUpdateEndpoint() throws Exception {
        MonitoredEndpoint updatedEndpoint = new MonitoredEndpoint("Updated", "https://test.com", 10, user);
        when(endpointRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(updatedEndpoint));
        when(endpointRepository.save(updatedEndpoint)).thenReturn(updatedEndpoint);


        mockMvc.perform(put("/rest/endpoints/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedEndpoint)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    public void shouldDeleteEndpoint() throws Exception {
        when(endpointRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(endpoint));

        mockMvc.perform(delete("/rest/endpoints/1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
