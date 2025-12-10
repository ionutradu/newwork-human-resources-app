package com.newwork.human_resources_app.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EmployeeSensitiveProfileDTO extends EmployeeProfileDTO {

    private BigDecimal monthlySalary;
    private List<FeedbackDTO> feedbacks;
    private List<AbsenceResponseDTO> absences;

}