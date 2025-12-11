package com.newwork.human_resources_app.domain.absence.repository;

import java.util.Collection;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AbsenceRepository extends MongoRepository<AbsenceRequest, String> {
    Collection<AbsenceRequest> findAllByEmployeeId(String employeeId);
}
