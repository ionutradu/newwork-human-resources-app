package com.newwork.human_resources_app.shared.mapper;

import com.newwork.human_resources_app.domain.absence.repository.AbsenceRequest;
import com.newwork.human_resources_app.shared.dto.absence.AbsenceDTO;
import com.newwork.human_resources_app.shared.dto.absence.AbsenceRequestDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AbsenceMapper {

    AbsenceRequest toEntity(AbsenceRequestDTO source, String employeeId);

    AbsenceDTO toDTO(AbsenceRequest source);
}
