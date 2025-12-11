package com.newwork.human_resources_app.domain.employee.service;

import com.newwork.human_resources_app.domain.absence.repository.AbsenceRepository;
import com.newwork.human_resources_app.domain.absence.repository.AbsenceRequest;
import com.newwork.human_resources_app.domain.employee.repository.Employee;
import com.newwork.human_resources_app.domain.employee.repository.EmployeeRepository;
import com.newwork.human_resources_app.domain.employee.repository.EmployeeRole;
import com.newwork.human_resources_app.domain.feedback.repository.Feedback;
import com.newwork.human_resources_app.domain.feedback.repository.FeedbackRepository;
import com.newwork.human_resources_app.shared.dto.EmployeeProfileDTO;
import com.newwork.human_resources_app.shared.dto.EmployeeSensitiveProfileDTO;
import com.newwork.human_resources_app.shared.exception.NotFoundException;
import com.newwork.human_resources_app.shared.mapper.AbsenceMapper;
import com.newwork.human_resources_app.shared.mapper.EmployeeMapper;
import com.newwork.human_resources_app.shared.mapper.FeedbackMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeMapper employeeMapper;
    private final AbsenceMapper absenceMapper;
    private final FeedbackMapper feedbackMapper;
    private final EmployeeRepository employeeRepository;
    private final FeedbackRepository feedbackRepository;
    private final AbsenceRepository absenceRepository;

    public Page<Employee> listEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    public Employee findByEmail(String email) {
        return employeeRepository
                .findByEmail(email)
                .orElseThrow(() -> new NotFoundException(email));
    }

    public EmployeeProfileDTO findById(
            String employeeId,
            String requesterId,
            Collection<? extends GrantedAuthority> requesterAuthorities) {
        var employee =
                employeeRepository
                        .findById(employeeId)
                        .orElseThrow(() -> new NotFoundException(employeeId));
        var requesterRoles = getRequesterRoles(requesterAuthorities);
        var requesterIsOwner = StringUtils.equals(requesterId, employeeId);

        if (requesterRoles.contains(EmployeeRole.MANAGER) || requesterIsOwner) {
            var absences = absenceRepository.findAllByEmployeeId(employeeId);
            var feedbacks = feedbackRepository.findAllByTargetEmployeeId(employeeId);
            return buildSensitiveProfileDTO(employee, absences, feedbacks);
        }

        return employeeMapper.toEmployeePublicProfileDTO(employee);
    }

    private EmployeeSensitiveProfileDTO buildSensitiveProfileDTO(
            Employee employee,
            Collection<AbsenceRequest> absenceRequests,
            Collection<Feedback> feedbacks) {

        var reviewerIds =
                feedbacks.stream().map(Feedback::getReviewerEmployeeId).collect(Collectors.toSet());
        var employeeById =
                employeeRepository.findAllById(reviewerIds).stream()
                        .collect(Collectors.toMap(Employee::getId, Function.identity()));

        var absenceDTOs = absenceRequests.stream().map(absenceMapper::toDTO).toList();
        var feedbackDTOs =
                feedbacks.stream()
                        .map(
                                feedback ->
                                        feedbackMapper.toDTO(
                                                feedback,
                                                employeeById.get(feedback.getReviewerEmployeeId())))
                        .toList();

        return employeeMapper.toEmployeeSensitiveProfileDTO(employee, absenceDTOs, feedbackDTOs);
    }

    private Set<EmployeeRole> getRequesterRoles(
            Collection<? extends GrantedAuthority> requesterAuthorities) {
        return requesterAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(roleString -> roleString.replaceFirst("ROLE_", ""))
                .map(EmployeeRole::valueOf)
                .collect(Collectors.toSet());
    }
}
