package com.newwork.human_resources_app.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeSensitiveProfileDTO extends EmployeeProfileDTO {

    private BigDecimal monthlySalary;
    private List<FeedbackDTO> feedbacks;
    private List<AbsenceDTO> absences;
    private Collection<EmployeeRoleDTO> roles;
}
