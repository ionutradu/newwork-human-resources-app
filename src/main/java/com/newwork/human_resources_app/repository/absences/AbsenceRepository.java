package com.newwork.human_resources_app.repository.absences;

import java.util.Collection;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AbsenceRepository extends MongoRepository<AbsenceRequest, String> {
    Collection<AbsenceRequest> findAllByEmployeeId(String employeeId);
}
