package com.newwork.human_resources_app.shared.dto.auth;

import com.newwork.human_resources_app.shared.dto.EmployeeRoleDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String firstName;
    private String lastName;
    private List<EmployeeRoleDTO> roles;
    private String token;
}
