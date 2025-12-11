package com.newwork.human_resources_app.domain.employee.repository;

import jakarta.validation.constraints.Email;
import java.math.BigDecimal;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("employees")
public class Employee {
    @Id private String id;

    @Email
    @Indexed(unique = true)
    private String email;

    private String firstName;
    private String lastName;

    private String passwordHash;

    private Set<EmployeeRole> roles;

    // Sensitive data
    private BigDecimal monthlySalary;
}
