package com.newwork.human_resources_app.repository.user;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
    Optional<Employee> findByEmail(String email);
}
