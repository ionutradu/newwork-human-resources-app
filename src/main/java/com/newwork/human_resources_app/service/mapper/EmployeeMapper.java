package com.newwork.human_resources_app.service.mapper;

import com.newwork.human_resources_app.repository.user.Employee;
import com.newwork.human_resources_app.repository.user.EmployeeRole;
import com.newwork.human_resources_app.web.dto.EmployeePublicProfileDTO;
import com.newwork.human_resources_app.web.dto.EmployeeRoleDTO;
import com.newwork.human_resources_app.web.dto.EmployeeSensitiveProfileDTO;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    Set<EmployeeRole> toRoles(Set<EmployeeRoleDTO> roles);

    Set<EmployeeRoleDTO> toRoleDTOs(Set<EmployeeRole> roles);

    EmployeeRoleDTO toDTO(EmployeeRole employeeRole);

    EmployeeRole toEntity(EmployeeRoleDTO employeeRoleDTO);

    EmployeePublicProfileDTO toEmployeePublicProfileDTO(Employee employee);

    EmployeeSensitiveProfileDTO toEmployeeSensitiveProfileDTO(Employee employee);

}
