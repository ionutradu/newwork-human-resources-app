package com.newwork.human_resources_app.domain.feedback.repository;

import java.util.Collection;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FeedbackRepository extends MongoRepository<Feedback, String> {
    Collection<Feedback> findAllByTargetEmployeeId(String employeeId);
}
