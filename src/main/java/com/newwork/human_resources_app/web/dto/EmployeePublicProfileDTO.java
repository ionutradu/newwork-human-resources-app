package com.newwork.human_resources_app.web.dto;

import lombok.Data;

@Data
public class EmployeePublicProfileDTO {

    private String id;
    private String firstName;
    private String lastName;
    private String jobTitle;

}