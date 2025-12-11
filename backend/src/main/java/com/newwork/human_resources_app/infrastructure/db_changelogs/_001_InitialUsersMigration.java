package com.newwork.human_resources_app.infrastructure.db_changelogs;

import com.newwork.human_resources_app.domain.absence.repository.AbsenceRepository;
import com.newwork.human_resources_app.domain.absence.repository.AbsenceRequest;
import com.newwork.human_resources_app.domain.absence.repository.AbsenceStatus;
import com.newwork.human_resources_app.domain.employee.repository.Employee;
import com.newwork.human_resources_app.domain.employee.repository.EmployeeRepository;
import com.newwork.human_resources_app.domain.employee.repository.EmployeeRole;
import com.newwork.human_resources_app.domain.feedback.repository.Feedback;
import com.newwork.human_resources_app.domain.feedback.repository.FeedbackRepository;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Migration script (Change Unit) to create the initial users for the application. */
@Slf4j
@RequiredArgsConstructor
@ChangeUnit(id = "InitialUsersMigration", order = "001", author = "ionut.radu")
public class _001_InitialUsersMigration {

    private static final String DEFAULT_PASSWORD = "SecurePassword";

    private static final Integer MIN_SALARY_VALUE = 5000;
    private static final Integer MAX_SALARY_VALUE = 10000;

    private final EmployeeRepository employeeRepository;
    private final AbsenceRepository absenceRepository;
    private final FeedbackRepository feedbackRepository;

    private final PasswordEncoder passwordEncoder;

    @Execution
    public void initialUsersMigration() {
        log.info("--- Mongock Migration 001: Inserting default users... ---");

        var manager =
                createEmployee("manager@app.com", Set.of(EmployeeRole.MANAGER), "John", "Doe");

        var coworker =
                createEmployee("coworker@app.com", Set.of(EmployeeRole.COWORKER), "Jane", "Smith");

        var employee =
                createEmployee(
                        "employee@app.com", Set.of(EmployeeRole.EMPLOYEE), "Alice", "Johnson");

        employeeRepository.saveAll(List.of(manager, coworker, employee));

        createAbsenceRequests(employee.getId());

        createFeedbacks(manager.getId(), coworker.getId(), employee.getId());

        log.info("--- Mongock Migration 001: Successfully inserted 3 users. ---");
    }

    private void createAbsenceRequests(String employeeId) {
        createAbsenceRequest(
                employeeId, LocalDate.now().plusDays(10), LocalDate.now().plusDays(15));

        createAbsenceRequest(
                employeeId, LocalDate.now().plusDays(23), LocalDate.now().plusDays(25));
    }

    private void createFeedbacks(String managerId, String coworkerId, String employeeId) {
        createFeedback(employeeId, coworkerId, "Feedback from Manager: Good job on the project.");

        createFeedback(employeeId, coworkerId, "Feedback from Coworker: Good job on the project.");
    }

    @RollbackExecution
    public void rollback() {
        employeeRepository.deleteAllById(List.of("manager", "coworker", "employee"));
        log.warn("--- Mongock Rollback 001: Removed initial users. ---");
    }

    private Employee createEmployee(
            String email, Collection<EmployeeRole> roles, String firstName, String lastName) {
        return Employee.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .passwordHash(passwordEncoder.encode(DEFAULT_PASSWORD))
                .firstName(firstName)
                .lastName(lastName)
                .roles(new HashSet<>(roles))
                .monthlySalary(generateRandomSalary())
                .build();
    }

    private AbsenceRequest createAbsenceRequest(String employeeId, LocalDate start, LocalDate end) {
        var absence =
                AbsenceRequest.builder()
                        .id(UUID.randomUUID().toString())
                        .employeeId(employeeId)
                        .startDate(start)
                        .endDate(end)
                        .reason("I have to go to the dentist")
                        .status(AbsenceStatus.PENDING)
                        .build();
        return absenceRepository.save(absence);
    }

    private Feedback createFeedback(
            String targetEmployeeId, String reviewerEmployeeId, String originalText) {
        var feedback =
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

    public static BigDecimal generateRandomSalary() {
        var randomInteger =
                ThreadLocalRandom.current().nextLong(MIN_SALARY_VALUE, MAX_SALARY_VALUE + 1);

        return BigDecimal.valueOf(randomInteger);
    }
}
