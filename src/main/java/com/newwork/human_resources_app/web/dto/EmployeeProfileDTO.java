package com.newwork.human_resources_app.web.dto;

import lombok.Data;

@Data
public abstract class EmployeeProfileDTO {

    private String id;
    private String email;
    private String firstName;
    private String lastName;

}