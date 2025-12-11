package com.newwork.human_resources_app.service.employee;

import com.newwork.human_resources_app.repository.absences.AbsenceRepository;
import com.newwork.human_resources_app.repository.absences.AbsenceRequest;
import com.newwork.human_resources_app.repository.feedback.Feedback;
import com.newwork.human_resources_app.repository.feedback.FeedbackRepository;
import com.newwork.human_resources_app.repository.user.Employee;
import com.newwork.human_resources_app.repository.user.EmployeeRepository;
import com.newwork.human_resources_app.repository.user.EmployeeRole;
import com.newwork.human_resources_app.service.mapper.AbsenceMapper;
import com.newwork.human_resources_app.service.mapper.EmployeeMapper;
import com.newwork.human_resources_app.service.mapper.FeedbackMapper;
import com.newwork.human_resources_app.web.dto.EmployeeProfileDTO;
import com.newwork.human_resources_app.web.dto.EmployeeRoleDTO;
import com.newwork.human_resources_app.web.dto.EmployeeSensitiveProfileDTO;
import com.newwork.human_resources_app.web.exceptions.NotFoundException;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeMapper employeeMapper;
    private final AbsenceMapper absenceMapper;
    private final FeedbackMapper feedbackMapper;
    private final EmployeeRepository userRepository;
    private final FeedbackRepository feedbackRepository;
    private final AbsenceRepository absenceRepository;
    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("hasAuthority('MANAGER')")
    public Employee createEmployee(String email, String password, Set<EmployeeRoleDTO> roles) {
        var employee =
                Employee.builder()
                        .id(UUID.randomUUID().toString())
                        .email(email)
                        .passwordHash(passwordEncoder.encode(password))
                        .roles(employeeMapper.toRoles(roles))
                        .build();

        return userRepository.save(employee);
    }

    public Page<Employee> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Employee findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(email));
    }

    public EmployeeProfileDTO findById(
            String employeeId,
            String requesterId,
            Collection<? extends GrantedAuthority> requesterAuthorities) {
        var employee =
                userRepository
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

        var absenceDTOs = absenceRequests.stream().map(absenceMapper::toDTO).toList();
        var feedbackDTOs = feedbacks.stream().map(feedbackMapper::toDTO).toList();

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
