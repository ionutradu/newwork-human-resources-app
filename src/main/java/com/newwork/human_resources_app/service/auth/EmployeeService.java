package com.newwork.human_resources_app.service.auth;

import com.newwork.human_resources_app.repository.user.Employee;
import com.newwork.human_resources_app.repository.user.EmployeeRepository;
import com.newwork.human_resources_app.service.mapper.EmployeeMapper;
import com.newwork.human_resources_app.web.dto.EmployeeRoleDTO;
import com.newwork.human_resources_app.web.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeMapper employeeMapper;
    private final EmployeeRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("hasAuthority('MANAGER')")
    public Employee createEmployee(String email, String password, Set<EmployeeRoleDTO> roles) {
        var user = Employee.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .roles(employeeMapper.toRoles(roles))
                .build();

        return userRepository.save(user);
    }

    public Page<Employee> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Employee findById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    public Employee findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(NotFoundException::new);
    }
}
