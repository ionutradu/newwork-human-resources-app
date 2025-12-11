package com.newwork.human_resources_app.domain.employee.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
    Optional<Employee> findByEmail(String email);
}
