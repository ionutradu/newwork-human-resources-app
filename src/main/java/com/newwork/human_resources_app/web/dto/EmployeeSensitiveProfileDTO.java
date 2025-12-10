package com.newwork.human_resources_app.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeSensitiveProfileDTO extends EmployeeProfileDTO {

    private BigDecimal monthlySalary;
    private List<FeedbackDTO> feedbacks;
    private List<AbsenceDTO> absences;

}