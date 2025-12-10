package com.newwork.human_resources_app.database_changelogs;

import com.newwork.human_resources_app.repository.user.Employee;
import com.newwork.human_resources_app.repository.user.EmployeeRepository;
import com.newwork.human_resources_app.repository.user.EmployeeRole;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Migration script (Change Unit) to create the initial users for the application.
 */
@Slf4j
@RequiredArgsConstructor
@ChangeUnit(id = "InitialUsersMigration", order = "001", author = "ionut.radu")
public class _001_InitialUsersMigration {

    private static final String DEFAULT_PASSWORD = "SecurePassword";

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Execution
    public void initialUsersMigration() {
        log.info("--- Mongock Migration 001: Inserting default users... ---");

        Employee manager = createEmployee(
                "manager",
                "manager@app.com",
                Set.of(EmployeeRole.MANAGER),
                "John",
                "Doe"
        );

        var hr = createEmployee(
                "coworker",
                "coworker@app.com",
                Set.of(EmployeeRole.COWORKER),
                "Jane",
                "Smith"
        );

        var employee = createEmployee(
                "employee",
                "employee@app.com",
                Set.of(EmployeeRole.EMPLOYEE),
                "Alice",
                "Johnson"
        );

        employeeRepository.saveAll(List.of(manager, hr, employee));
        log.info("--- Mongock Migration 001: Successfully inserted 3 users. ---");
    }

    @RollbackExecution
    public void rollback() {
        employeeRepository.deleteAllById(List.of("manager", "coworker", "employee"));
        log.warn("--- Mongock Rollback 001: Removed initial users. ---");
    }

    private Employee createEmployee(String username, String email, Collection<EmployeeRole> roles, String firstName, String lastName) {
        return Employee.builder()
                .id(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(DEFAULT_PASSWORD))
                .firstName(firstName)
                .lastName(lastName)
                .roles(new HashSet<>(roles))
                .build();
    }
}