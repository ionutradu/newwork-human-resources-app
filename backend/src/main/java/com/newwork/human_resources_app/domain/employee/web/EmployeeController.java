package com.newwork.human_resources_app.domain.employee.web;

import com.newwork.human_resources_app.domain.employee.service.EmployeeService;
import com.newwork.human_resources_app.shared.dto.EmployeeProfileDTO;
import com.newwork.human_resources_app.shared.mapper.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeMapper employeeMapper;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGER') or #id == authentication.principal")
    public EmployeeProfileDTO getSensitiveUserProfile(
            Authentication authentication, @PathVariable String id) {
        var authorities = authentication.getAuthorities();
        var requesterId = (String) authentication.getPrincipal();
        return employeeService.findById(id, requesterId, authorities);
    }

    @GetMapping("/public/{id}")
    @PreAuthorize("hasAnyAuthority('COWORKER', 'MANAGER') or #id == authentication.principal")
    public EmployeeProfileDTO getPublicUserProfile(
            Authentication authentication, @PathVariable String id) {
        var authorities = authentication.getAuthorities();
        var requesterId = (String) authentication.getPrincipal();
        return employeeService.findById(id, requesterId, authorities);
    }

    @GetMapping("/public")
    @PreAuthorize("hasAnyAuthority('COWORKER', 'MANAGER')")
    public ResponseEntity<Page<EmployeeProfileDTO>> listEmployeePublicProfiles(Pageable pageable) {
        var users = employeeService.listEmployees(pageable);
        var userDTOs = users.map(employeeMapper::toEmployeePublicProfileDTO);
        return ResponseEntity.ok(userDTOs);
    }
}
