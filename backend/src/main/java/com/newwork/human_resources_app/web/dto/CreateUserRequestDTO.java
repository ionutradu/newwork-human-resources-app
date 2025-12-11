package com.newwork.human_resources_app.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequestDTO {
    @Email @NotBlank private String email;
    @NotBlank private String password;
    @NotBlank private String firstName;
    @NotBlank private String lastName;

    @NotEmpty private Set<EmployeeRoleDTO> roles;
}
