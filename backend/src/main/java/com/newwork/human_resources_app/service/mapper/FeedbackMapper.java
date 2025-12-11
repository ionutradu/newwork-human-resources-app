package com.newwork.human_resources_app.service.mapper;

import com.newwork.human_resources_app.repository.feedback.Feedback;
import com.newwork.human_resources_app.web.dto.FeedbackDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {

    FeedbackDTO toDTO(Feedback source);
}
