package com.newwork.human_resources_app.employee;

import com.newwork.human_resources_app.repository.absences.AbsenceRepository;
import com.newwork.human_resources_app.repository.absences.AbsenceRequest;
import com.newwork.human_resources_app.repository.absences.AbsenceStatus;
import com.newwork.human_resources_app.repository.feedback.Feedback;
import com.newwork.human_resources_app.repository.feedback.FeedbackRepository;
import com.newwork.human_resources_app.repository.user.Employee;
import com.newwork.human_resources_app.repository.user.EmployeeRepository;
import com.newwork.human_resources_app.repository.user.EmployeeRole;
import com.newwork.human_resources_app.web.dto.AbsenceDTO;
import com.newwork.human_resources_app.web.dto.AuthRequestDTO;
import com.newwork.human_resources_app.web.dto.AuthResponseDTO;
import com.newwork.human_resources_app.web.dto.EmployeeProfileDTO;
import com.newwork.human_resources_app.web.dto.EmployeeSensitiveProfileDTO;
import com.newwork.human_resources_app.web.dto.FeedbackDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeProfileIntegrationTest {

    private static final String COMMON_PASS = "SecurePassword123!";
    private static final String AUTH_URL = "/auth/login";
    private static final String EMPLOYEES_URL = "/employees";

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

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.0");

    private Map<EmployeeRole, List<Employee>> employeesByRole;
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

        var allEmployees = List.of(
                createEmployee("manager1@test.com", "Manager", "One", EmployeeRole.MANAGER, "10000.00"),
                createEmployee("manager2@test.com", "Manager", "Two", EmployeeRole.MANAGER, "12000.00"),
                createEmployee("coworker1@test.com", "CoWorker", "One", EmployeeRole.COWORKER, "5000.00"),
                createEmployee("coworker2@test.com", "CoWorker", "Two", EmployeeRole.COWORKER, "6000.00"),
                createEmployee("employee1@test.com", "Employee", "One", EmployeeRole.EMPLOYEE, "3000.00"),
                createEmployee("employee2@test.com", "Employee", "Two", EmployeeRole.EMPLOYEE, "4000.00")
        );

        employeeRepository.saveAll(allEmployees);
        employeesByRole = allEmployees.stream().collect(Collectors.groupingBy(e -> e.getRoles().iterator().next()));
        manager = employeesByRole.get(EmployeeRole.MANAGER).get(0);
        coworker = employeesByRole.get(EmployeeRole.COWORKER).get(0);
        employee = employeesByRole.get(EmployeeRole.EMPLOYEE).get(0);
    }

    @Test
    @DisplayName("Manager can fetch public and sensitive employee profile")
    void testManagerCanViewSpecificUserSensitiveAndPublicData() {
        // Given a manager token
        var managerEntity = getAuthenticationHeaders(manager.getEmail());

        // Given a target employee
        var targetId = employee.getId();

        // When fetching the sensitive employee profile
        var sensitiveResponse = restTemplate.exchange(
                EMPLOYEES_URL + "/" + targetId,
                HttpMethod.GET,
                managerEntity,
                EmployeeSensitiveProfileDTO.class
        );

        // Then sensitive data is returned
        assertEquals(HttpStatus.OK, sensitiveResponse.getStatusCode());
        var sensitiveDto = sensitiveResponse.getBody();
        assertNotNull(sensitiveDto);
        assertEquals(employee.getFirstName(), sensitiveDto.getFirstName());

        assertNotNull(sensitiveDto.getMonthlySalary());
        assertEquals(employee.getMonthlySalary(), sensitiveDto.getMonthlySalary());

        // Given a coworker token
        var coworkerEntity = getAuthenticationHeaders(coworker.getEmail());

        // When fetching an employee public profile
        var publicResponse = restTemplate.exchange(
                EMPLOYEES_URL + "/public/" + targetId,
                HttpMethod.GET,
                coworkerEntity,
                EmployeeSensitiveProfileDTO.class // try to map it to a sensitive profile data
        );

        // Then public profile is returned with no sensitive data
        assertEquals(HttpStatus.OK, publicResponse.getStatusCode());
        var publicDto = publicResponse.getBody();
        assertNotNull(publicDto);
        assertEquals(employee.getFirstName(), publicDto.getFirstName());

        assertEquals(targetId, publicDto.getId());
    }

    @Test
    @DisplayName("Login works with correct password")
    void testLoginOnlyWorksWithCorrectPassword() {
        var authRequest = new AuthRequestDTO("manager1@test.com", COMMON_PASS);
        var response = restTemplate.postForEntity(AUTH_URL, authRequest, AuthResponseDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Login does not work with incorrect password")
    void testLoginDoesNotWorkWithIncorrectPassword() {
        var authRequestWithWrongPass = new AuthRequestDTO("manager1@test.com", "wrongpassword");
        var responseWithWrongPass = restTemplate.postForEntity(AUTH_URL, authRequestWithWrongPass, Void.class);
        assertEquals(HttpStatus.UNAUTHORIZED, responseWithWrongPass.getStatusCode());
    }

    @Test
    @DisplayName("Manager role can view employee absences")
    void testManagerCanViewSpecificUserAbsences() {
        // Given a manager token
        var entity = getAuthenticationHeaders(manager.getEmail());
        var targetId = employee.getId();

        // Given 2 absence requests
        var absence1 = createAbsenceRequest(targetId, LocalDate.now().plusDays(10), LocalDate.now().plusDays(15), AbsenceStatus.PENDING);
        var absence2 = createAbsenceRequest(targetId, LocalDate.now().plusDays(20), LocalDate.now().plusDays(22), AbsenceStatus.APPROVED);

        // When requesting sensitive employee profile
        var sensitiveResponse = restTemplate.exchange(
                EMPLOYEES_URL + "/" + targetId,
                HttpMethod.GET,
                entity,
                EmployeeSensitiveProfileDTO.class
        );

        // Then response is received containing absences
        assertEquals(HttpStatus.OK, sensitiveResponse.getStatusCode());
        var sensitiveDto = sensitiveResponse.getBody();
        assertNotNull(sensitiveDto);
        assertNotNull(sensitiveDto.getAbsences());
        assertEquals(2, sensitiveDto.getAbsences().size());

        // Verify absences have the correct database ids
        var returnedAbsenceIds = sensitiveDto.getAbsences().stream()
                .map(AbsenceDTO::getId)
                .collect(Collectors.toSet());
        assertTrue(returnedAbsenceIds.contains(absence1.getId()));
        assertTrue(returnedAbsenceIds.contains(absence2.getId()));
    }

    @Test
    @DisplayName("Manager role can view employee feedbacks")
    void testManagerCanViewSpecificEmployeeFeedbacks() {
        // Given a manager token
        var entity = getAuthenticationHeaders(manager.getEmail());
        var targetId = employee.getId();

        // Given feedbacks
        var feedback1 = createFeedback(targetId, manager.getId(), "Feedback from Manager: Good job on the project.");
        var feedback2 = createFeedback(targetId, coworker.getId(), "Feedback from Coworker: Need to improve communication.");

        // When requesting employee sensitive profile
        var sensitiveResponse = restTemplate.exchange(
                EMPLOYEES_URL + "/" + targetId,
                HttpMethod.GET,
                entity,
                EmployeeSensitiveProfileDTO.class
        );

        // Then profile is received with feedbacks
        assertEquals(HttpStatus.OK, sensitiveResponse.getStatusCode());
        var sensitiveDto = sensitiveResponse.getBody();
        assertNotNull(sensitiveDto);
        assertNotNull(sensitiveDto.getFeedbacks());
        assertEquals(2, sensitiveDto.getFeedbacks().size());

        // Verify feedback ids are the same as database ids
        var returnedFeedbackIds = sensitiveDto.getFeedbacks().stream()
                .map(FeedbackDTO::getId)
                .collect(Collectors.toSet());
        assertTrue(returnedFeedbackIds.contains(feedback1.getId()));
        assertTrue(returnedFeedbackIds.contains(feedback2.getId()));
    }

    private AbsenceRequest createAbsenceRequest(String employeeId, LocalDate start, LocalDate end, AbsenceStatus status) {
        AbsenceRequest absence = AbsenceRequest.builder()
                .id(UUID.randomUUID().toString())
                .employeeId(employeeId)
                .startDate(start)
                .endDate(end)
                .reason("Test Reason")
                .status(status)
                .build();
        return absenceRepository.save(absence);
    }

    private Feedback createFeedback(String targetEmployeeId, String reviewerEmployeeId, String originalText) {
        Feedback feedback = Feedback.builder()
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