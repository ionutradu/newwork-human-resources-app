package com.newwork.human_resources_app.employee;

import com.newwork.human_resources_app.client.hugging_face.HuggingFaceChatClient;
import com.newwork.human_resources_app.client.hugging_face.dto.HFChatResponse;
import com.newwork.human_resources_app.client.hugging_face.dto.HFChoice;
import com.newwork.human_resources_app.client.hugging_face.dto.HFMessage;
import com.newwork.human_resources_app.repository.absences.AbsenceRepository;
import com.newwork.human_resources_app.repository.feedback.FeedbackRepository;
import com.newwork.human_resources_app.repository.user.Employee;
import com.newwork.human_resources_app.repository.user.EmployeeRepository;
import com.newwork.human_resources_app.repository.user.EmployeeRole;
import com.newwork.human_resources_app.web.dto.AbsenceActionDTO;
import com.newwork.human_resources_app.web.dto.AbsenceActionRequestDTO;
import com.newwork.human_resources_app.web.dto.AbsenceRequestDTO;
import com.newwork.human_resources_app.web.dto.AuthRequestDTO;
import com.newwork.human_resources_app.web.dto.AuthResponseDTO;
import com.newwork.human_resources_app.web.dto.FeedbackRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeActionsIntegrationTest {

    private static final String COMMON_PASS = "SecurePassword123!";
    private static final String AUTH_URL = "/auth/login";
    private static final String ABSENCE_URL = "/employees/absence";
    private static final String FEEDBACK_URL_TEMPLATE = "/employees/%s/feedback";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private HuggingFaceChatClient huggingFaceChatClient;

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    private static final String AI_POLISHED_TEXT = "Collaboration could be strengthened through more active listening and proactive sharing of ideas during team discussions. (mocked response)";

    private Map<EmployeeRole, List<Employee>> employeesByRole;
    private Employee coWorker;
    private Employee employee;

    @DynamicPropertySource
    static void setMongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setupTestData() {
        employeeRepository.deleteAll();
        absenceRepository.deleteAll();
        feedbackRepository.deleteAll();

        var allEmployees = List.of(
                createEmployee("manager1@test.com", "Manager", "One", EmployeeRole.MANAGER, "10000.00"),
                createEmployee("coworker1@test.com", "CoWorker", "One", EmployeeRole.COWORKER, "5000.00"),
                createEmployee("employee1@test.com", "Employee", "One", EmployeeRole.EMPLOYEE, "3000.00")
        );

        employeeRepository.saveAll(allEmployees);
        employeesByRole = allEmployees.stream().collect(Collectors.groupingBy(e -> e.getRoles().iterator().next()));
        coWorker = employeesByRole.get(EmployeeRole.COWORKER).get(0);
        employee = employeesByRole.get(EmployeeRole.EMPLOYEE).get(0);

        var mockResponse = new HFChatResponse(UUID.randomUUID().toString(), "chat.completion", 1765365241L, "test-ai-model", List.of(new HFChoice(0, new HFMessage("assistant", AI_POLISHED_TEXT), "stop")));
        given(huggingFaceChatClient.generateChatCompletion(any())).willReturn(mockResponse);
    }

    @Test
    @DisplayName("Authenticated user can request absence")
    void testAuthenticatedUserCanRequestAbsence() {
        // Given an employee requesting absence
        var entity = getAuthenticationHeaders(employee.getEmail());

        var startDate = LocalDate.now().plusDays(1);
        var endDate = startDate.plusDays(5);
        var reason = "Vacation request for summer break.";

        var absenceRequestDTO = new AbsenceRequestDTO(startDate, endDate, reason);

        // Verify no absence requests in database
        assertEquals(0, absenceRepository.count());

        // When calling the absence API
        var response = restTemplate.exchange(
                ABSENCE_URL,
                HttpMethod.POST,
                new HttpEntity<>(absenceRequestDTO, entity.getHeaders()),
                Void.class
        );

        // Then absence is accepted
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

        // Then absence is persisted in database
        assertEquals(1, absenceRepository.count());
        var savedAbsence = absenceRepository.findAll().iterator().next();
        assertEquals(employee.getId(), savedAbsence.getEmployeeId());
        assertEquals(startDate, savedAbsence.getStartDate());
        assertEquals(endDate, savedAbsence.getEndDate());
        assertEquals(reason, savedAbsence.getReason());
    }

    @Test
    @DisplayName("Coworker can leave feedback for another employee")
    void testCoworkerCanLeaveFeedbackForAnotherEmployee() {
        // Given a coworker token
        var entity = getAuthenticationHeaders(coWorker.getEmail());

        // Given an Employee colleague and their feedback
        var targetEmployeeId = employee.getId();
        var originalFeedback = "I think you should improve your collaboration skills.";

        var feedbackRequestDTO = new FeedbackRequestDTO(originalFeedback);
        var url = String.format(FEEDBACK_URL_TEMPLATE, targetEmployeeId);

        // Verify no feedbacks in database
        assertEquals(0, feedbackRepository.count());

        // When calling the feedback API
        var response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(feedbackRequestDTO, entity.getHeaders()),
                Void.class
        );

        // Then feedback is accepted
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

        // Then feedback is persisted in database
        assertEquals(1, feedbackRepository.count());
        var savedFeedback = feedbackRepository.findAll().iterator().next();
        assertEquals(targetEmployeeId, savedFeedback.getTargetEmployeeId());
        assertEquals(coWorker.getId(), savedFeedback.getReviewerEmployeeId());
        assertEquals(originalFeedback, savedFeedback.getOriginalText());
        assertEquals(AI_POLISHED_TEXT, savedFeedback.getPolishedText());
    }


    private HttpEntity<HttpHeaders> getAuthenticationHeaders(String email) {
        var token = authenticateAndGetToken(email);
        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new HttpEntity<>(headers);
    }

    private Employee createEmployee(String email, String firstName, String lastName, EmployeeRole role, String salary) {
        return Employee.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .passwordHash(passwordEncoder.encode(COMMON_PASS))
                .roles(Set.of(role))
                .monthlySalary(new BigDecimal(salary))
                .build();
    }

    private String authenticateAndGetToken(String email) {
        var authRequest = new AuthRequestDTO(email, COMMON_PASS);
        var response = restTemplate.postForEntity(AUTH_URL, authRequest, AuthResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getToken());

        return response.getBody().getToken();
    }
}