package com.newwork.human_resources_app.domain.auth.web;

import com.newwork.human_resources_app.domain.auth.service.AuthService;
import com.newwork.human_resources_app.domain.employee.service.EmployeeService;
import com.newwork.human_resources_app.shared.dto.auth.AuthRequestDTO;
import com.newwork.human_resources_app.shared.dto.auth.AuthResponseDTO;
import com.newwork.human_resources_app.shared.exception.BadCredentialsException;
import com.newwork.human_resources_app.shared.mapper.EmployeeMapper;
import jakarta.validation.Valid;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final EmployeeService employeeService;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid AuthRequestDTO authRequest) {

        var user = employeeService.findByEmail(authRequest.getEmail());

        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException();
        }

        var roles = user.getRoles();
        var roleDTOs =
                employeeMapper.toRoleDTOs(roles).stream().sorted().collect(Collectors.toList());
        var token = authService.generateToken(user.getId(), roles);

        return ResponseEntity.ok(
                new AuthResponseDTO(user.getFirstName(), user.getLastName(), roleDTOs, token));
    }
}
