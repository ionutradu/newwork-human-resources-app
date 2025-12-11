package com.newwork.human_resources_app.integration_tests.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.newwork.human_resources_app.domain.absence.repository.AbsenceRepository;
import com.newwork.human_resources_app.domain.absence.repository.AbsenceRequest;
import com.newwork.human_resources_app.domain.absence.repository.AbsenceStatus;
import com.newwork.human_resources_app.domain.employee.repository.Employee;
import com.newwork.human_resources_app.domain.employee.repository.EmployeeRepository;
import com.newwork.human_resources_app.domain.employee.repository.EmployeeRole;
import com.newwork.human_resources_app.domain.feedback.repository.Feedback;
import com.newwork.human_resources_app.domain.feedback.repository.FeedbackRepository;
import com.newwork.human_resources_app.shared.dto.auth.AuthRequestDTO;
import com.newwork.human_resources_app.shared.dto.auth.AuthResponseDTO;
import com.newwork.human_resources_app.shared.dto.manager.ManagerUpdateAbsenceRequestDTO;
import com.newwork.human_resources_app.shared.dto.manager.ManagerUpdateEmployeeDTO;
import com.newwork.human_resources_app.shared.dto.manager.ManagerUpdateFeedbackDTO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ManagerEditIntegrationTest {

    private static final String COMMON_PASS = "SecurePassword123!";
    private static final String AUTH_URL = "/auth/login";
    private static final String MANAGER_URL = "/manager";

    @Autowired private TestRestTemplate restTemplate;

    @Autowired private EmployeeRepository employeeRepository;

    @Autowired private AbsenceRepository absenceRepository;

    @Autowired private FeedbackRepository feedbackRepository;

    @Autowired private PasswordEncoder passwordEncoder;

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.0");

    private Employee manager;
    private Employee coworker;
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
                                Set.of(EmployeeRole.MANAGER),
                                "10000.00"),
                        createEmployee(
                                "coworker1@test.com",
                                "CoWorker",
                                "One",
                                Set.of(EmployeeRole.COWORKER),
                                "5000.00"),
                        createEmployee(
                                "employee1@test.com",
                                "Employee",
                                "One",
                                Set.of(EmployeeRole.EMPLOYEE),
                                "3000.00"));

        employeeRepository.saveAll(allEmployees);
        Map<EmployeeRole, List<Employee>> employeesByRole =
                allEmployees.stream()
                        .collect(Collectors.groupingBy(e -> e.getRoles().iterator().next()));

        manager = employeesByRole.get(EmployeeRole.MANAGER).get(0);
        coworker = employeesByRole.get(EmployeeRole.COWORKER).get(0);
        employee = employeesByRole.get(EmployeeRole.EMPLOYEE).get(0);
    }

    private Employee createEmployee(
            String email,
            String firstName,
            String lastName,
            Set<EmployeeRole> roles,
            String salary) {
        return Employee.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .passwordHash(passwordEncoder.encode(COMMON_PASS))
                .roles(roles)
                .monthlySalary(new BigDecimal(salary))
                .build();
    }

    private AbsenceRequest createAbsenceRequest(
            String employeeId, LocalDate start, LocalDate end, AbsenceStatus status) {
        AbsenceRequest absence =
                AbsenceRequest.builder()
                        .id(UUID.randomUUID().toString())
                        .employeeId(employeeId)
                        .startDate(start)
                        .endDate(end)
                        .reason("Test Reason")
                        .status(status)
                        .build();
        return absenceRepository.save(absence);
    }

    private Feedback createFeedback(
            String targetEmployeeId, String reviewerEmployeeId, String originalText) {
        Feedback feedback =
                Feedback.builder()
                        .id(UUID.randomUUID().toString())
                        .targetEmployeeId(targetEmployeeId)
                        .reviewerEmployeeId(reviewerEmployeeId)
                        .originalText(originalText)
                        .polishedText("Polished: " + originalText)
                        .createdAt(LocalDateTime.now())
                        .build();
        return feedbackRepository.save(feedback);
    }

    private HttpEntity<HttpHeaders> getAuthenticationHeaders(String email) {
        var token = authenticateAndGetToken(email);
        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new HttpEntity<>(headers);
    }

    private String authenticateAndGetToken(String email) {
        var authRequest = new AuthRequestDTO(email, COMMON_PASS);
        var response = restTemplate.postForEntity(AUTH_URL, authRequest, AuthResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getToken());

        return response.getBody().getToken();
    }

    @Test
    @DisplayName("Manager can update employee details")
    void testManagerCanUpdateEmployee() {
        // Given
        var employeeToUpdate =
                employeeRepository.save(
                        createEmployee(
                                "old.email@test.com",
                                "OldName",
                                "OldSurname",
                                Set.of(EmployeeRole.EMPLOYEE),
                                "1000.00"));
        var managerEntity = getAuthenticationHeaders(manager.getEmail());
        var updateRequest =
                ManagerUpdateEmployeeDTO.builder()
                        .firstName("NewName")
                        .monthlySalary(new BigDecimal("1500.50"))
                        .roles(Set.of(EmployeeRole.COWORKER, EmployeeRole.EMPLOYEE))
                        .build();

        // When
        var response =
                restTemplate.exchange(
                        MANAGER_URL + "/employees/" + employeeToUpdate.getId(),
                        HttpMethod.PATCH,
                        new HttpEntity<>(updateRequest, managerEntity.getHeaders()),
                        Employee.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var updatedEmployee = response.getBody();
        assertNotNull(updatedEmployee);
        assertEquals("NewName", updatedEmployee.getFirstName());
        assertEquals(
                "OldSurname",
                updatedEmployee.getLastName(),
                "LastName should remain the same if not provided");
        assertEquals(new BigDecimal("1500.50"), updatedEmployee.getMonthlySalary());
        assertTrue(updatedEmployee.getRoles().contains(EmployeeRole.COWORKER));

        var employeeInDb = employeeRepository.findById(employeeToUpdate.getId()).orElseThrow();
        assertEquals("NewName", employeeInDb.getFirstName());
    }

    @Test
    @DisplayName("Non-manager cannot update employee details")
    void testNonManagerCannotUpdateEmployee() {
        // Given
        var employeeToUpdate = employee;
        var coworkerEntity = getAuthenticationHeaders(coworker.getEmail());
        var updateRequest = ManagerUpdateEmployeeDTO.builder().firstName("IllegalUpdate").build();

        // When
        var response =
                restTemplate.exchange(
                        MANAGER_URL + "/employees/" + employeeToUpdate.getId(),
                        HttpMethod.PATCH,
                        new HttpEntity<>(updateRequest, coworkerEntity.getHeaders()),
                        Void.class);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

        var employeeInDb = employeeRepository.findById(employeeToUpdate.getId()).orElseThrow();
        assertNotEquals("IllegalUpdate", employeeInDb.getFirstName());
    }

    @Test
    @DisplayName("Manager receives 404 when updating non-existent employee")
    void testManagerCannotUpdateNonExistentEmployee() {
        // Given
        var nonExistentId = "non-existent-id";
        var managerEntity = getAuthenticationHeaders(manager.getEmail());
        var updateRequest = ManagerUpdateEmployeeDTO.builder().firstName("Test").build();

        // When
        var response =
                restTemplate.exchange(
                        MANAGER_URL + "/employees/" + nonExistentId,
                        HttpMethod.PATCH,
                        new HttpEntity<>(updateRequest, managerEntity.getHeaders()),
                        Void.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Manager receives 400 on invalid employee update data (salary)")
    void testManagerReceives400OnInvalidEmployeeUpdate() {
        // Given
        var employeeToUpdate = employee;
        var managerEntity = getAuthenticationHeaders(manager.getEmail());
        var invalidRequest =
                ManagerUpdateEmployeeDTO.builder()
                        .monthlySalary(new BigDecimal("-10.00")) // Invalid: must be positive
                        .build();

        // When
        var response =
                restTemplate.exchange(
                        MANAGER_URL + "/employees/" + employeeToUpdate.getId(),
                        HttpMethod.PATCH,
                        new HttpEntity<>(invalidRequest, managerEntity.getHeaders()),
                        Void.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Manager can update absence request status and sets processedBy")
    void testManagerCanUpdateAbsenceRequestStatus() {
        // Given
        var absenceRequest =
                createAbsenceRequest(
                        employee.getId(),
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(5),
                        AbsenceStatus.PENDING);
        var managerEntity = getAuthenticationHeaders(manager.getEmail());
        var updateRequest =
                ManagerUpdateAbsenceRequestDTO.builder().status(AbsenceStatus.APPROVED).build();

        // When
        var response =
                restTemplate.exchange(
                        MANAGER_URL + "/absences/" + absenceRequest.getId(),
                        HttpMethod.PATCH,
                        new HttpEntity<>(updateRequest, managerEntity.getHeaders()),
                        AbsenceRequest.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var updatedAbsence = response.getBody();
        assertNotNull(updatedAbsence);
        assertEquals(AbsenceStatus.APPROVED, updatedAbsence.getStatus());
        assertEquals(
                manager.getId(),
                updatedAbsence.getProcessedBy(),
                "ProcessedBy should be the manager's ID");

        var absenceInDb = absenceRepository.findById(absenceRequest.getId()).orElseThrow();
        assertEquals(AbsenceStatus.APPROVED, absenceInDb.getStatus());
    }

    @Test
    @DisplayName("Non-manager cannot update absence request")
    void testNonManagerCannotUpdateAbsenceRequest() {
        // Given
        var absenceRequest =
                createAbsenceRequest(
                        employee.getId(),
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(5),
                        AbsenceStatus.PENDING);
        var coworkerEntity = getAuthenticationHeaders(coworker.getEmail());
        var updateRequest =
                ManagerUpdateAbsenceRequestDTO.builder().status(AbsenceStatus.APPROVED).build();

        // When
        var response =
                restTemplate.exchange(
                        MANAGER_URL + "/absences/" + absenceRequest.getId(),
                        HttpMethod.PATCH,
                        new HttpEntity<>(updateRequest, coworkerEntity.getHeaders()),
                        Void.class);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

        var absenceInDb = absenceRepository.findById(absenceRequest.getId()).orElseThrow();
        assertEquals(AbsenceStatus.PENDING, absenceInDb.getStatus());
    }

    @Test
    @DisplayName("Manager receives 404 when updating non-existent absence request")
    void testManagerCannotUpdateNonExistentAbsence() {
        // Given
        var nonExistentId = "non-existent-absence-id";
        var managerEntity = getAuthenticationHeaders(manager.getEmail());
        var updateRequest =
                ManagerUpdateAbsenceRequestDTO.builder().status(AbsenceStatus.APPROVED).build();

        // When
        var response =
                restTemplate.exchange(
                        MANAGER_URL + "/absences/" + nonExistentId,
                        HttpMethod.PATCH,
                        new HttpEntity<>(updateRequest, managerEntity.getHeaders()),
                        Void.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Manager can update feedback text")
    void testManagerCanUpdateFeedback() {
        // Given
        var feedback =
                createFeedback(employee.getId(), coworker.getId(), "Needs improvement in X.");
        var managerEntity = getAuthenticationHeaders(manager.getEmail());
        var updateRequest =
                ManagerUpdateFeedbackDTO.builder()
                        .polishedText("Revised: Great job overall. Area X is a growth opportunity.")
                        .build();

        // When
        var response =
                restTemplate.exchange(
                        MANAGER_URL + "/feedbacks/" + feedback.getId(),
                        HttpMethod.PATCH,
                        new HttpEntity<>(updateRequest, managerEntity.getHeaders()),
                        Feedback.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var updatedFeedback = response.getBody();
        assertNotNull(updatedFeedback);
        assertEquals(
                "Needs improvement in X.",
                updatedFeedback.getOriginalText(),
                "Original text should be preserved");
        assertEquals(
                "Revised: Great job overall. Area X is a growth opportunity.",
                updatedFeedback.getPolishedText());

        var feedbackInDb = feedbackRepository.findById(feedback.getId()).orElseThrow();
        assertEquals(
                "Revised: Great job overall. Area X is a growth opportunity.",
                feedbackInDb.getPolishedText());
    }

    @Test
    @DisplayName("Non-manager cannot update feedback")
    void testNonManagerCannotUpdateFeedback() {
        // Given
        var feedback = createFeedback(employee.getId(), coworker.getId(), "Initial text.");
        var coworkerEntity = getAuthenticationHeaders(coworker.getEmail());
        var updateRequest =
                ManagerUpdateFeedbackDTO.builder().polishedText("Illegal Update").build();

        // When
        var response =
                restTemplate.exchange(
                        MANAGER_URL + "/feedbacks/" + feedback.getId(),
                        HttpMethod.PATCH,
                        new HttpEntity<>(updateRequest, coworkerEntity.getHeaders()),
                        Void.class);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

        var feedbackInDb = feedbackRepository.findById(feedback.getId()).orElseThrow();
        assertNotEquals("Illegal Update", feedbackInDb.getPolishedText());
    }

    @Test
    @DisplayName("Manager receives 404 when updating non-existent feedback")
    void testManagerCannotUpdateNonExistentFeedback() {
        // Given
        var nonExistentId = "non-existent-feedback-id";
        var managerEntity = getAuthenticationHeaders(manager.getEmail());
        var updateRequest = ManagerUpdateFeedbackDTO.builder().polishedText("Test").build();

        // When
        var response =
                restTemplate.exchange(
                        MANAGER_URL + "/feedbacks/" + nonExistentId,
                        HttpMethod.PATCH,
                        new HttpEntity<>(updateRequest, managerEntity.getHeaders()),
                        Void.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
