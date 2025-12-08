package com.newwork.human_resources_app.repository.absences;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;

public interface AbsenceRepository extends MongoRepository<AbsenceRequest, String> {
    Collection<AbsenceRequest> findAllByEmployeeId(String employeeId);
}
