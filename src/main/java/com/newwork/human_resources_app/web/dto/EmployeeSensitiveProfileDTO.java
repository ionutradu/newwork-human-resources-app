package com.newwork.human_resources_app.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSensitiveProfileDTO {

    private String id;
    private String email;
    private String firstName;
    private String lastName;

    private BigDecimal monthlySalary;

}