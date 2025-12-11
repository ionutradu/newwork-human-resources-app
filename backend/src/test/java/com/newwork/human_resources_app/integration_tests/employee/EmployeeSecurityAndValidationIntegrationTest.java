package com.newwork.human_resources_app.integration_tests.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.newwork.human_resources_app.core.client.hugging_face.HuggingFaceChatClient;
import com.newwork.human_resources_app.core.client.hugging_face.dto.HFChatResponse;
import com.newwork.human_resources_app.domain.absence.repository.AbsenceRepository;
import com.newwork.human_resources_app.domain.employee.repository.Employee;
import com.newwork.human_resources_app.domain.employee.repository.EmployeeRepository;
import com.newwork.human_resources_app.domain.employee.repository.EmployeeRole;
import com.newwork.human_resources_app.domain.feedback.repository.FeedbackRepository;
import com.newwork.human_resources_app.shared.dto.EmployeeSensitiveProfileDTO;
import com.newwork.human_resources_app.shared.dto.FeedbackRequestDTO;
import com.newwork.human_resources_app.shared.dto.absence.AbsenceActionDTO;
import com.newwork.human_resources_app.shared.dto.absence.AbsenceActionRequestDTO;
import com.newwork.human_resources_app.shared.dto.absence.AbsenceRequestDTO;
import com.newwork.human_resources_app.shared.dto.auth.AuthRequestDTO;
import com.newwork.human_resources_app.shared.dto.auth.AuthResponseDTO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
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

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeSecurityAndValidationIntegrationTest {

    private static final String COMMON_PASS = "SecurePassword123!";
    private static final String AUTH_URL = "/auth/login";
    private static final String EMPLOYEES_URL = "/employees";
    private static final String ABSENCE_URL = "/employees/absence";
    private static final String FEEDBACK_URL_TEMPLATE = "/employees/%s/feedback";

    @Autowired private TestRestTemplate restTemplate;

    @Autowired private EmployeeRepository employeeRepository;

    @Autowired private AbsenceRepository absenceRepository;

    @Autowired private FeedbackRepository feedbackRepository;

    @Autowired private PasswordEncoder passwordEncoder;

    @MockitoBean private HuggingFaceChatClient huggingFaceChatClient;

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.0");

    private Map<EmployeeRole, List<Employee>> employeesByRole;
    private Employee manager;
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

        var allEmployees =
                List.of(
                        createEmployee(
                                "manager1@test.com",
                                "Manager",
                                "One",
                                EmployeeRole.MANAGER,
                                "10000.00"),
                        createEmployee(
                                "coworker1@test.com",
                                "CoWorker",
                                "One",
                                EmployeeRole.COWORKER,
                                "5000.00"),
                        createEmployee(
                                "employee1@test.com",
                                "Employee",
                                "One",
                                EmployeeRole.EMPLOYEE,
                                "3000.00"));

        employeeRepository.saveAll(allEmployees);
        employeesByRole =
                allEmployees.stream()
                        .collect(Collectors.groupingBy(e -> e.getRoles().iterator().next()));
        manager = employeesByRole.get(EmployeeRole.MANAGER).get(0);
        coWorker = employeesByRole.get(EmployeeRole.COWORKER).get(0);
        employee = employeesByRole.get(EmployeeRole.EMPLOYEE).get(0);

        // Mock AI success response for any test that might call it
        var mockSuccessResponse =
                new HFChatResponse(
                        UUID.randomUUID().toString(),
                        "chat.completion",
                        1L,
                        "test-ai-model",
                        List.of(
                                new com.newwork.human_resources_app.core.client.hugging_face.dto
                                        .HFChoice(
                                        0,
                                        new com.newwork.human_resources_app.core.client.hugging_face
                                                .dto.HFMessage(
                                                "assistant",
                                                "Collaboration could be strengthened."),
                                        "stop")));
        given(huggingFaceChatClient.generateChatCompletion(any())).willReturn(mockSuccessResponse);
    }

    @Test
    @DisplayName("Unauthenticated user cannot access secured endpoints")
    void testUnauthenticatedUserCannotAccessSecuredEndpoint() {
        // Given no authentication headers
        var headers = new HttpHeaders();

        // When attempting to access a secured endpoint (e.g., list all employees)
        var response =
                restTemplate.exchange(
                        EMPLOYEES_URL, HttpMethod.GET, new HttpEntity<>(headers), Void.class);

        // Then status is Unauthorized
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Employee with role 'MANAGER' can list public profiles")
    void testManagerCannotViewAllUsersPublicData() {
        // Given a Manager token
        var entity = getAuthenticationHeaders(manager.getEmail());

        // When attempting to access public profile list
        // CORECTAT: Verific?m direct statusul r?spunsului
        var response =
                restTemplate.exchange(
                        EMPLOYEES_URL + "/public",
                        HttpMethod.GET,
                        entity,
                        String.class // Folosim String.class pentru a citi eroarea 403
                        );

        // Then response status is Forbidden
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Expected status 200 Ok.");
    }

    @Test
    @DisplayName("Employee with role 'EMPLOYEE' can fetch their own profile")
    void testEmployeeCanViewOwnSensitiveProfile() {
        // Given an Employee token
        var entity = getAuthenticationHeaders(employee.getEmail());
        var ownId = employee.getId();

        // When calling the sensitive endpoint for own id
        var sensitiveResponse =
                restTemplate.exchange(
                        EMPLOYEES_URL + "/" + ownId,
                        HttpMethod.GET,
                        entity,
                        EmployeeSensitiveProfileDTO.class);

        // Then status is OK and sensitive data is present
        assertEquals(HttpStatus.OK, sensitiveResponse.getStatusCode());
        assertNotNull(sensitiveResponse.getBody());
        assertEquals(employee.getMonthlySalary(), sensitiveResponse.getBody().getMonthlySalary());
    }

    @Test
    @DisplayName("Employee with role 'EMPLOYEE' cannot fetch a colleague's sensitive profile")
    void testEmployeeCannotViewColleagueSensitiveProfile() {
        // Given an Employee token
        var entity = getAuthenticationHeaders(employee.getEmail());
        var colleagueId = coWorker.getId();

        // When calling the sensitive endpoint for a colleague's id
        var response =
                restTemplate.exchange(
                        EMPLOYEES_URL + "/" + colleagueId, HttpMethod.GET, entity, Void.class);

        // Then status is Forbidden
        assertEquals(
                HttpStatus.FORBIDDEN, response.getStatusCode(), "Expected status 403 Forbidden.");
    }

    @Test
    @DisplayName("Employee with role 'EMPLOYEE' cannot fetch a colleague's sensitive profile")
    void testEmployeeCannotViewColleaguePublicProfile() {
        // Given an Employee token
        var entity = getAuthenticationHeaders(employee.getEmail());
        var colleagueId = coWorker.getId();

        // When calling the public endpoint for a colleague's id
        var response =
                restTemplate.exchange(
                        EMPLOYEES_URL + "/public/" + colleagueId,
                        HttpMethod.GET,
                        entity,
                        Void.class);

        // Then status is Forbidden
        assertEquals(
                HttpStatus.FORBIDDEN, response.getStatusCode(), "Expected status 403 Forbidden.");
    }

    @Test
    @DisplayName("Employee with role 'EMPLOYEE' cannot leave a feedback")
    void testEmployeeCannotLeaveFeedback() {
        // Given an Employee token
        var entity = getAuthenticationHeaders(employee.getEmail());
        var targetEmployeeId = coWorker.getId();
        var feedbackRequestDTO = new FeedbackRequestDTO("Good job!");
        var url = String.format(FEEDBACK_URL_TEMPLATE, targetEmployeeId);

        // Given initial state
        assertEquals(0, feedbackRepository.count());

        // When attempting to leave feedback
        var response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        new HttpEntity<>(feedbackRequestDTO, entity.getHeaders()),
                        Void.class);

        // Then status is Forbidden and no feedbacks are saved in database
        assertEquals(
                HttpStatus.FORBIDDEN, response.getStatusCode(), "Expected status 403 Forbidden.");
        assertEquals(0, feedbackRepository.count());
    }

    @Test
    @DisplayName("Absence end date cannot be before start date")
    void testAbsenceRequestFailsWhenEndDateIsBeforeStartDate() {
        // Given an Employee token
        var entity = getAuthenticationHeaders(employee.getEmail());

        // Given an invalid DTO (end date is one day before start date)
        var startDate = LocalDate.now().plusDays(5);
        var endDate = startDate.minusDays(1);
        var absenceRequestDTO = new AbsenceRequestDTO(startDate, endDate, "Invalid dates");

        // Given initial state
        assertEquals(0, absenceRepository.count());

        // When calling the API
        var response =
                restTemplate.exchange(
                        ABSENCE_URL,
                        HttpMethod.POST,
                        new HttpEntity<>(absenceRequestDTO, entity.getHeaders()),
                        String.class);

        // Then response status is Bad Request
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "Expected status 400 Bad Request (Validation failed).");
        assertEquals(0, absenceRepository.count());
    }

    @Test
    @DisplayName("Absence start date cannot be in the past")
    void testAbsenceRequestFailsWhenStartDateIsInThePast() {
        // Given an Employee token
        var entity = getAuthenticationHeaders(employee.getEmail());

        // Given an invalid DTO (start date is in the past)
        var startDate = LocalDate.now().minusDays(1);
        var endDate = LocalDate.now().plusDays(5);
        var absenceRequestDTO = new AbsenceRequestDTO(startDate, endDate, "Past start date");

        // Given initial state
        assertEquals(0, absenceRepository.count());

        // When calling the API
        var response =
                restTemplate.exchange(
                        ABSENCE_URL,
                        HttpMethod.POST,
                        new HttpEntity<>(absenceRequestDTO, entity.getHeaders()),
                        String.class);

        // Then response status is Bad Request
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "Expected status 400 Bad Request (Validation failed).");
        assertEquals(0, absenceRepository.count());
    }

    @Test
    @DisplayName("Feedback text is rejected when text is too long")
    void testFeedbackRequestFailsWhenTextIsTooLong() {
        // Given a Coworker token
        var entity = getAuthenticationHeaders(coWorker.getEmail());
        var targetEmployeeId = employee.getId();
        var veryLongFeedback = "A".repeat(1001); // Assuming max length is 1000

        var feedbackRequestDTO = new FeedbackRequestDTO(veryLongFeedback);
        var url = String.format(FEEDBACK_URL_TEMPLATE, targetEmployeeId);

        // Given initial state
        assertEquals(0, feedbackRepository.count());

        // When calling the feedback API
        var response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        new HttpEntity<>(feedbackRequestDTO, entity.getHeaders()),
                        String.class);

        // Then response status is Bad Request
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "Expected status 400 Bad Request (Validation failed).");
        assertEquals(0, feedbackRepository.count());
    }

    @Test
    @DisplayName(
            "Feedback polishing will be retried 5 times, then it will fallback to original text if AI service is not available")
    void testFeedbackIsSavedEvenIfAiServiceFails() {
        // Given original feedback
        var originalFeedback = "I think you should improve your collaboration skills.";
        // Given AI service will throw a runtime exception
        given(huggingFaceChatClient.generateChatCompletion(any()))
                .willThrow(new RuntimeException("Mocked AI Service Failure"));

        var entity = getAuthenticationHeaders(coWorker.getEmail());
        var targetEmployeeId = employee.getId();
        var feedbackRequestDTO = new FeedbackRequestDTO(originalFeedback);
        var url = String.format(FEEDBACK_URL_TEMPLATE, targetEmployeeId);
        assertEquals(0, feedbackRepository.count());

        // When leaving a feedback
        var response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        new HttpEntity<>(feedbackRequestDTO, entity.getHeaders()),
                        Void.class);

        // Then feedback is accepted and stored in database
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(1, feedbackRepository.count());

        var savedFeedback = feedbackRepository.findAll().iterator().next();
        assertEquals(originalFeedback, savedFeedback.getOriginalText());

        // Then feedback text fallbacks to original text
        assertTrue(
                savedFeedback.getPolishedText().contains(originalFeedback),
                "Polished text should contain the original text if AI fails.");

        // Verify the AI client was called 5 times before throwing the exception
        verify(huggingFaceChatClient, times(5)).generateChatCompletion(any());
    }

    @Test
    @DisplayName("Unauthenticated user is unauthorized (401)")
    void testUnauthenticatedUserIsUnauthorized() {
        // Given an unauthenticated request to a secure endpoint
        var absenceRequestDTO =
                new AbsenceRequestDTO(
                        LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), "Test reason");
        var headers = new HttpHeaders();

        // When calling the absence API
        var response =
                restTemplate.exchange(
                        ABSENCE_URL,
                        HttpMethod.POST,
                        new HttpEntity<>(absenceRequestDTO, headers),
                        Void.class);

        // Then response is Unauthorized (401)
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Authenticated 'EMPLOYEE' cannot access manager endpoints")
    void testEmployeeCannotAccessManagerEndpoint() {
        // Given an employee token
        var employeeToken = authenticateAndGetToken(employee.getEmail());
        var entity =
                new HttpEntity<>(
                        new HttpHeaders() {
                            {
                                setBearerAuth(employeeToken);
                            }
                        });

        // Assuming a Manager-only endpoint for processing absence:
        var MANAGER_PROCESS_ABSENCE_URL = "/manager/absences/some-id/process";

        // When employee tries to access manager endpoint
        var processDTO = new AbsenceActionRequestDTO(AbsenceActionDTO.APPROVE);
        var response =
                restTemplate.exchange(
                        MANAGER_PROCESS_ABSENCE_URL,
                        HttpMethod.POST,
                        new HttpEntity<>(processDTO, entity.getHeaders()),
                        Void.class);

        // Then response is Forbidden (403)
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    private HttpEntity<HttpHeaders> getAuthenticationHeaders(String email) {
        var token = authenticateAndGetToken(email);
        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new HttpEntity<>(headers);
    }

    private Employee createEmployee(
            String email, String firstName, String lastName, EmployeeRole role, String salary) {
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
