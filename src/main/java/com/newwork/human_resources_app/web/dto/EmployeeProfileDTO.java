package com.newwork.human_resources_app.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeProfileDTO {

    private String id;
    private String email;
    private String firstName;
    private String lastName;
}
