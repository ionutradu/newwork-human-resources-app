package com.newwork.human_resources_app.web.dto.manager;

import com.newwork.human_resources_app.repository.user.EmployeeRole;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerUpdateEmployeeDTO {
    @Size(min = 2, max = 50)
    private String firstName;

    @Size(min = 2, max = 50)
    private String lastName;

    @Email
    private String email;

    @DecimalMin(value = "0.00", inclusive = false)
    private BigDecimal monthlySalary;

    private Set<EmployeeRole> roles;
}
