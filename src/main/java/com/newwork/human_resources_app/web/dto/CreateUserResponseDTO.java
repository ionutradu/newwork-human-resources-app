package com.newwork.human_resources_app.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class CreateUserResponseDTO {
    @Email
    @NotBlank
    private String email;
    @NotEmpty
    private Set<EmployeeRoleDTO> roles;
}
