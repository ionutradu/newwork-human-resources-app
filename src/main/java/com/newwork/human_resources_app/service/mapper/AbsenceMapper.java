package com.newwork.human_resources_app.service.mapper;

import com.newwork.human_resources_app.repository.absences.AbsenceRequest;
import com.newwork.human_resources_app.web.dto.AbsenceRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AbsenceMapper {

    AbsenceRequest toEntity(AbsenceRequestDTO source, String employeeId);

}
