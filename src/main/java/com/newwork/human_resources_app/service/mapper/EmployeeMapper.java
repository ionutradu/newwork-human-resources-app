package com.newwork.human_resources_app.service.mapper;

import com.newwork.human_resources_app.repository.user.Employee;
import com.newwork.human_resources_app.repository.user.EmployeeRole;
import com.newwork.human_resources_app.web.dto.AbsenceDTO;
import com.newwork.human_resources_app.web.dto.EmployeeProfileDTO;
import com.newwork.human_resources_app.web.dto.EmployeeRoleDTO;
import com.newwork.human_resources_app.web.dto.EmployeeSensitiveProfileDTO;
import com.newwork.human_resources_app.web.dto.FeedbackDTO;
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
