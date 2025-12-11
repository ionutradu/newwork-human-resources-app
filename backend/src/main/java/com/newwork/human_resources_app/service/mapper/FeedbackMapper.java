package com.newwork.human_resources_app.service.mapper;

import com.newwork.human_resources_app.repository.feedback.Feedback;
import com.newwork.human_resources_app.repository.user.Employee;
import com.newwork.human_resources_app.web.dto.FeedbackDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {

    @Mapping(target = "id", source = "source.id")
    @Mapping(
            target = "reviewerName",
            expression = "java(employee.getFirstName() + \" \" + employee.getLastName())")
    FeedbackDTO toDTO(Feedback source, Employee employee);
}
