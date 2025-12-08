package com.newwork.human_resources_app.web.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EmployeeSensitiveProfileDTO {
    private String username;
    private String firstName;
    private String lastName;
    private BigDecimal monthlySalary;
}