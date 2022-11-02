package com.job.task;

import com.job.task.entity.endpoint.MonitoredEndpoint;
import com.job.task.entity.result.MonitoringResult;
import com.job.task.repository.MonitoredEndpointRepository;
import com.job.task.entity.user.User;
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

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EndpointControllerTest {

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

        mockMvc.perform(get("/endpoints/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("https://test.com")));
    }

    @Test
    public void shouldReturnForbidden() throws Exception {
        User batman = new User("Batman", "batman@example.com", "dcb20f8a-5657-4f1b-9f7f-ce65739b359e");
        MonitoredEndpoint endpointOfAnotherUser = new MonitoredEndpoint("Github", "https://github.com", 10, batman);

        when(endpointRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(endpointOfAnotherUser));

        mockMvc.perform(get("/endpoints/1"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/endpoints/2"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnAllEndpoints() throws Exception {
        when(endpointRepository.findAllByOwner(user)).thenReturn(endpoints);

        mockMvc.perform(get("/endpoints"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("https://test.com")));
    }

    @Test
    public void shouldExecutePost() throws Exception {
        mockMvc.perform(post("/endpoints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(endpoint)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldExecutePut() throws Exception {
        when(endpointRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(endpoint));

        mockMvc.perform(put("/endpoints/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(endpoint)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnResults() throws Exception {

        List<MonitoringResult> results = Arrays.asList(new MonitoringResult(200, "whatever", endpoint));

        when(endpointRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(endpoint));
        when(resultRepository.findTop10ByEndpointOrderByIdDesc(endpoint)).thenReturn(results);

        mockMvc.perform(get("/endpoints/1/results"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("whatever")));
    }

}
