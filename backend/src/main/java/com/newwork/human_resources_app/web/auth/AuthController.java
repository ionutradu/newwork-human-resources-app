package com.newwork.human_resources_app.web.auth;

import com.newwork.human_resources_app.repository.user.EmployeeRole;
import com.newwork.human_resources_app.service.auth.JwtService;
import com.newwork.human_resources_app.service.employee.EmployeeService;
import com.newwork.human_resources_app.service.mapper.EmployeeMapper;
import com.newwork.human_resources_app.web.dto.AuthRequestDTO;
import com.newwork.human_resources_app.web.dto.AuthResponseDTO;
import com.newwork.human_resources_app.web.dto.EmployeeRoleDTO;
import com.newwork.human_resources_app.web.exceptions.BadCredentialsException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final EmployeeService employeeService;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid AuthRequestDTO authRequest) {

        var user = employeeService.findByEmail(authRequest.getEmail());

        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException();
        }

        var roles = user.getRoles();
        var roleDTOs = employeeMapper.toRoleDTOs(roles).stream().sorted().collect(Collectors.toList());
        var token = jwtService.generateToken(user.getId(), roles);

        return ResponseEntity.ok(new AuthResponseDTO(user.getFirstName(), user.getLastName(), roleDTOs, token));
    }
}
