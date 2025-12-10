package com.newwork.human_resources_app.employee;

import com.newwork.human_resources_app.repository.user.Employee;
import com.newwork.human_resources_app.repository.user.EmployeeRepository;
import com.newwork.human_resources_app.repository.user.EmployeeRole;
import com.newwork.human_resources_app.web.dto.AuthRequestDTO;
import com.newwork.human_resources_app.web.dto.AuthResponseDTO;
import com.newwork.human_resources_app.web.dto.EmployeePublicProfileDTO;
import com.newwork.human_resources_app.web.dto.EmployeeSensitiveProfileDTO;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    private PasswordEncoder passwordEncoder;

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    private Map<EmployeeRole, List<Employee>> employeesByRole;
    private Employee manager;

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
    }

    @Test
    void testManagerCanViewAllUsersSensitiveData() {
        var entity = getAuthenticationHeaders();

        var sensitiveResponse = restTemplate.exchange(
                EMPLOYEES_URL,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Page<EmployeeSensitiveProfileDTO>>() {
                }
        );

        assertEquals(HttpStatus.OK, sensitiveResponse.getStatusCode());
        assertNotNull(sensitiveResponse.getBody());
        assertEquals(6, sensitiveResponse.getBody().getTotalElements());

        var firstSensitiveUser = sensitiveResponse.getBody().getContent().get(0);
        assertNotNull(firstSensitiveUser.getMonthlySalary());

        var publicResponse = restTemplate.exchange(
                EMPLOYEES_URL + "/public",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Page<EmployeePublicProfileDTO>>() {
                }
        );

        assertEquals(HttpStatus.OK, publicResponse.getStatusCode());
        assertNotNull(publicResponse.getBody());
        assertEquals(6, publicResponse.getBody().getTotalElements());
    }

    @Test
    void testManagerCanViewSpecificUserSensitiveAndPublicData() {
        var entity = getAuthenticationHeaders();

        var targetUser = employeesByRole.get(EmployeeRole.EMPLOYEE).get(0);
        var targetId = targetUser.getId();

        var sensitiveResponse = restTemplate.exchange(
                EMPLOYEES_URL + "/" + targetId,
                HttpMethod.GET,
                entity,
                EmployeeSensitiveProfileDTO.class
        );

        assertEquals(HttpStatus.OK, sensitiveResponse.getStatusCode());
        var sensitiveDto = sensitiveResponse.getBody();
        assertNotNull(sensitiveDto);
        assertEquals(targetUser.getFirstName(), sensitiveDto.getFirstName());

        assertNotNull(sensitiveDto.getMonthlySalary());
        assertEquals(targetUser.getMonthlySalary(), sensitiveDto.getMonthlySalary());

        var publicResponse = restTemplate.exchange(
                EMPLOYEES_URL + "/public/" + targetId,
                HttpMethod.GET,
                entity,
                EmployeePublicProfileDTO.class
        );

        assertEquals(HttpStatus.OK, publicResponse.getStatusCode());
        var publicDto = publicResponse.getBody();
        assertNotNull(publicDto);
        assertEquals(targetUser.getFirstName(), publicDto.getFirstName());

        assertEquals(targetId, publicDto.getId());
    }

    @Test
    void testLoginOnlyWorksWithCorrectPassword() {
        var authRequest = new AuthRequestDTO("manager1@test.com", COMMON_PASS);
        var response = restTemplate.postForEntity(AUTH_URL, authRequest, AuthResponseDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var authRequestWithWrongPass = new AuthRequestDTO("manager1@test.com", "wrongpassword");
        var responseWithWrongPass = restTemplate.postForEntity(AUTH_URL, authRequestWithWrongPass, AuthResponseDTO.class);
        assertEquals(HttpStatus.UNAUTHORIZED, responseWithWrongPass.getStatusCode());
    }

    private HttpEntity<HttpHeaders> getAuthenticationHeaders() {
        var token = authenticateAndGetToken(manager.getEmail());
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