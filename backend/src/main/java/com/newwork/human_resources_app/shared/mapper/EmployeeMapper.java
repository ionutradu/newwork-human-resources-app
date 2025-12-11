package com.newwork.human_resources_app.shared.mapper;

import com.newwork.human_resources_app.domain.employee.repository.Employee;
import com.newwork.human_resources_app.domain.employee.repository.EmployeeRole;
import com.newwork.human_resources_app.shared.dto.EmployeeProfileDTO;
import com.newwork.human_resources_app.shared.dto.EmployeeRoleDTO;
import com.newwork.human_resources_app.shared.dto.EmployeeSensitiveProfileDTO;
import com.newwork.human_resources_app.shared.dto.FeedbackDTO;
import com.newwork.human_resources_app.shared.dto.absence.AbsenceDTO;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    Set<EmployeeRole> toRoles(Set<EmployeeRoleDTO> roles);

    Set<EmployeeRoleDTO> toRoleDTOs(Set<EmployeeRole> roles);

    EmployeeRoleDTO toDTO(EmployeeRole employeeRole);

    EmployeeRole toEntity(EmployeeRoleDTO employeeRoleDTO);

    EmployeeProfileDTO toEmployeePublicProfileDTO(Employee employee);

    EmployeeSensitiveProfileDTO toEmployeeSensitiveProfileDTO(
            Employee employee, List<AbsenceDTO> absences, List<FeedbackDTO> feedbacks);
}
