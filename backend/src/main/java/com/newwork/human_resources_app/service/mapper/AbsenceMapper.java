package com.newwork.human_resources_app.service.mapper;

import com.newwork.human_resources_app.repository.absences.AbsenceRequest;
import com.newwork.human_resources_app.web.dto.AbsenceDTO;
import com.newwork.human_resources_app.web.dto.AbsenceRequestDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AbsenceMapper {

    AbsenceRequest toEntity(AbsenceRequestDTO source, String employeeId);

    AbsenceDTO toDTO(AbsenceRequest source);
}
