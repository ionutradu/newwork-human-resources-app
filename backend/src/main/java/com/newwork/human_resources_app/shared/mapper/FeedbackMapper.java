package com.newwork.human_resources_app.shared.mapper;

import com.newwork.human_resources_app.domain.employee.repository.Employee;
import com.newwork.human_resources_app.domain.feedback.repository.Feedback;
import com.newwork.human_resources_app.shared.dto.FeedbackDTO;
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
