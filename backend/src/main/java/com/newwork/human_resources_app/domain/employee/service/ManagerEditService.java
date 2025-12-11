package com.newwork.human_resources_app.domain.employee.service;

import com.newwork.human_resources_app.domain.absence.repository.AbsenceRepository;
import com.newwork.human_resources_app.domain.absence.repository.AbsenceRequest;
import com.newwork.human_resources_app.domain.employee.repository.Employee;
import com.newwork.human_resources_app.domain.employee.repository.EmployeeRepository;
import com.newwork.human_resources_app.domain.feedback.repository.Feedback;
import com.newwork.human_resources_app.domain.feedback.repository.FeedbackRepository;
import com.newwork.human_resources_app.shared.dto.manager.ManagerUpdateAbsenceRequestDTO;
import com.newwork.human_resources_app.shared.dto.manager.ManagerUpdateEmployeeDTO;
import com.newwork.human_resources_app.shared.dto.manager.ManagerUpdateFeedbackDTO;
import com.newwork.human_resources_app.shared.exception.NotFoundException;
import java.util.Optional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@PreAuthorize("hasAuthority('MANAGER')")
public class ManagerEditService {

    private final EmployeeRepository employeeRepository;
    private final AbsenceRepository absenceRepository;
    private final FeedbackRepository feedbackRepository;

    public ManagerEditService(
            EmployeeRepository employeeRepository,
            AbsenceRepository absenceRepository,
            FeedbackRepository feedbackRepository) {
        this.employeeRepository = employeeRepository;
        this.absenceRepository = absenceRepository;
        this.feedbackRepository = feedbackRepository;
    }

    public Employee updateEmployee(String id, ManagerUpdateEmployeeDTO dto) {
        Employee employee =
                employeeRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                "Employee not found by id: %s.".formatted(id)));

        Optional.ofNullable(dto.getFirstName()).ifPresent(employee::setFirstName);
        Optional.ofNullable(dto.getLastName()).ifPresent(employee::setLastName);
        Optional.ofNullable(dto.getEmail()).ifPresent(employee::setEmail);
        Optional.ofNullable(dto.getMonthlySalary()).ifPresent(employee::setMonthlySalary);
        Optional.ofNullable(dto.getRoles()).ifPresent(employee::setRoles);

        return employeeRepository.save(employee);
    }

    public AbsenceRequest updateAbsenceRequest(
            String id, ManagerUpdateAbsenceRequestDTO dto, String managerId) {
        AbsenceRequest request =
                absenceRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                "AbsenceRequest not found by id: %s."
                                                        .formatted(id)));

        Optional.ofNullable(dto.getStartDate()).ifPresent(request::setStartDate);
        Optional.ofNullable(dto.getEndDate()).ifPresent(request::setEndDate);
        Optional.ofNullable(dto.getReason()).ifPresent(request::setReason);
        Optional.ofNullable(dto.getStatus())
                .ifPresent(
                        newStatus -> {
                            request.setStatus(newStatus);
                            request.setProcessedBy(managerId);
                        });

        return absenceRepository.save(request);
    }

    public Feedback updateFeedback(String id, ManagerUpdateFeedbackDTO dto) {
        Feedback feedback =
                feedbackRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                "Feedback not found by id: %s.".formatted(id)));

        Optional.ofNullable(dto.getOriginalText()).ifPresent(feedback::setOriginalText);
        Optional.ofNullable(dto.getPolishedText()).ifPresent(feedback::setPolishedText);

        return feedbackRepository.save(feedback);
    }
}
