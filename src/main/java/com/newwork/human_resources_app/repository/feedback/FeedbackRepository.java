package com.newwork.human_resources_app.repository.feedback;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;

public interface FeedbackRepository extends MongoRepository<Feedback, String> {
    Collection<Feedback> findAllByTargetEmployeeId(String email);
    Collection<Feedback> findAllByReviewerEmployeeId(String email);
}
