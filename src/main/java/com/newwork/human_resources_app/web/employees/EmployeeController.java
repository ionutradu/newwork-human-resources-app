package com.newwork.human_resources_app.web.employees;

import com.newwork.human_resources_app.service.auth.EmployeeService;
import com.newwork.human_resources_app.service.feedback.EmployeeActionsService;
import com.newwork.human_resources_app.service.mapper.EmployeeMapper;
import com.newwork.human_resources_app.web.dto.CreateUserRequestDTO;
import com.newwork.human_resources_app.web.dto.CreateUserResponseDTO;
import com.newwork.human_resources_app.web.dto.EmployeeProfileDTO;
import com.newwork.human_resources_app.web.dto.EmployeePublicProfileDTO;
import com.newwork.human_resources_app.web.dto.EmployeeSensitiveProfileDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeActionsService employeeActionsService;
    private final EmployeeMapper employeeMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<CreateUserResponseDTO> createUser(@RequestBody @Valid CreateUserRequestDTO request) {
        var user = employeeService.createEmployee(request.getEmail(), request.getPassword(), request.getRoles());
        return ResponseEntity.ok(new CreateUserResponseDTO(user.getEmail(), employeeMapper.toRoleDTOs(user.getRoles())));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGER') or #id == principal.id")
    public EmployeeProfileDTO getSensitiveUserProfile(Authentication authentication,
                                                               @PathVariable String id) {
        var authorities = authentication.getAuthorities();
        return employeeService.findById(id, authorities);
    }

    @GetMapping("/public/{id}")
    @PreAuthorize("hasAuthority('COWORKER') or #id == principal.id")
    public EmployeeProfileDTO getPublicUserProfile(Authentication authentication, @PathVariable String id) {
        var authorities = authentication.getAuthorities();
        return employeeService.findById(id, authorities);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Page<EmployeeSensitiveProfileDTO>> listEmployeeSensitiveProfiles(Pageable pageable) {
        var users = employeeService.listUsers(pageable);
        var userDTOs = users.map(employee -> employeeMapper.toEmployeeSensitiveProfileDTO(employee, null, null));
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/public")
    @PreAuthorize("hasAuthority('COWORKER')")
    public ResponseEntity<Page<EmployeePublicProfileDTO>> listEmployeePublicProfiles(Pageable pageable) {
        var users = employeeService.listUsers(pageable);
        var userDTOs = users.map(employeeMapper::toEmployeePublicProfileDTO);
        return ResponseEntity.ok(userDTOs);
    }

}
