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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeMapper employeeMapper;
    @Mock
    private AbsenceMapper absenceMapper;
    @Mock
    private FeedbackMapper feedbackMapper;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private AbsenceRepository absenceRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private final String EMPLOYEE_ID = "emp123";
    private final String MANAGER_ID = "mgr999";
    private final String COWORKER_ID = "cwr456";
    private final String EMPLOYEE_EMAIL = "test@app.com";
    private Employee targetEmployee;
    private EmployeeProfileDTO publicProfileDTO;
    private EmployeeSensitiveProfileDTO sensitiveProfileDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        targetEmployee = new Employee();
        targetEmployee.setId(EMPLOYEE_ID);
        targetEmployee.setEmail(EMPLOYEE_EMAIL);
        targetEmployee.setFirstName("Alice");

        publicProfileDTO = new EmployeeProfileDTO();
        publicProfileDTO.setId(EMPLOYEE_ID);

        sensitiveProfileDTO = new EmployeeSensitiveProfileDTO();
        sensitiveProfileDTO.setId(EMPLOYEE_ID);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void findByEmail_Found_ReturnsEmployee() {
        // Arrange
        when(employeeRepository.findByEmail(EMPLOYEE_EMAIL)).thenReturn(Optional.of(targetEmployee));

        // Act
        var result = employeeService.findByEmail(EMPLOYEE_EMAIL);

        // Assert
        assertNotNull(result);
        assertEquals(EMPLOYEE_EMAIL, result.getEmail());
        verify(employeeRepository, times(1)).findByEmail(EMPLOYEE_EMAIL);
    }

    @Test
    void findByEmail_NotFound_ThrowsNotFoundException() {
        // Arrange
        when(employeeRepository.findByEmail(EMPLOYEE_EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () ->
                employeeService.findByEmail(EMPLOYEE_EMAIL)
        );
        verify(employeeRepository, times(1)).findByEmail(EMPLOYEE_EMAIL);
    }

    @Test
    void listUsers_ReturnsPagedEmployees() {
        // Arrange
        var employeePage = new PageImpl<>(List.of(targetEmployee), pageable, 1);
        when(employeeRepository.findAll(pageable)).thenReturn(employeePage);

        // Act
        var result = employeeService.listUsers(pageable);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        verify(employeeRepository, times(1)).findAll(pageable);
    }

    @Test
    void findById_RequesterIsManager_ReturnsSensitiveDTO() {
        // Arrange
        var managerAuthorities = List.of(new SimpleGrantedAuthority("ROLE_" + EmployeeRole.MANAGER.name()));
        var absences = List.of(new AbsenceRequest());
        var feedbacks = List.of(new Feedback());

        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(targetEmployee));
        when(absenceRepository.findAllByEmployeeId(EMPLOYEE_ID)).thenReturn(absences);
        when(feedbackRepository.findAllByTargetEmployeeId(EMPLOYEE_ID)).thenReturn(feedbacks);

        when(employeeRepository.findAllById(any())).thenReturn(Collections.emptyList());
        when(absenceMapper.toDTO(any(AbsenceRequest.class))).thenReturn(any());
        when(feedbackMapper.toDTO(any(Feedback.class), any())).thenReturn(any());
        when(employeeMapper.toEmployeeSensitiveProfileDTO(any(), any(), any())).thenReturn(sensitiveProfileDTO);

        // Act
        var result = employeeService.findById(EMPLOYEE_ID, MANAGER_ID, managerAuthorities);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof EmployeeSensitiveProfileDTO);

        verify(absenceRepository, times(1)).findAllByEmployeeId(EMPLOYEE_ID);
        verify(feedbackRepository, times(1)).findAllByTargetEmployeeId(EMPLOYEE_ID);
        verify(employeeMapper, times(1)).toEmployeeSensitiveProfileDTO(any(), any(), any());
        verify(employeeMapper, never()).toEmployeePublicProfileDTO(any());
    }

    @Test
    void findById_RequesterIsOwner_ReturnsSensitiveDTO() {
        // Arrange
        var employeeAuthorities = List.of(new SimpleGrantedAuthority("ROLE_" + EmployeeRole.EMPLOYEE.name()));
        var absences = List.of(new AbsenceRequest());
        var feedbacks = List.of(new Feedback());

        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(targetEmployee));
        when(absenceRepository.findAllByEmployeeId(EMPLOYEE_ID)).thenReturn(absences);
        when(feedbackRepository.findAllByTargetEmployeeId(EMPLOYEE_ID)).thenReturn(feedbacks);

        when(employeeRepository.findAllById(any())).thenReturn(Collections.emptyList());
        when(absenceMapper.toDTO(any(AbsenceRequest.class))).thenReturn(any());
        when(feedbackMapper.toDTO(any(Feedback.class), any())).thenReturn(any());
        when(employeeMapper.toEmployeeSensitiveProfileDTO(any(), any(), any())).thenReturn(sensitiveProfileDTO);

        // Act
        var result = employeeService.findById(EMPLOYEE_ID, EMPLOYEE_ID, employeeAuthorities);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof EmployeeSensitiveProfileDTO);

        verify(absenceRepository, times(1)).findAllByEmployeeId(EMPLOYEE_ID);
        verify(feedbackRepository, times(1)).findAllByTargetEmployeeId(EMPLOYEE_ID);
        verify(employeeMapper, times(1)).toEmployeeSensitiveProfileDTO(any(), any(), any());
        verify(employeeMapper, never()).toEmployeePublicProfileDTO(any());
    }

    @Test
    void findById_RequesterIsCoworker_ReturnsPublicDTO() {
        // Arrange
        var coworkerAuthorities = List.of(new SimpleGrantedAuthority("ROLE_" + EmployeeRole.COWORKER.name()));

        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(targetEmployee));
        when(employeeMapper.toEmployeePublicProfileDTO(targetEmployee)).thenReturn(publicProfileDTO);

        // Act
        var result = employeeService.findById(EMPLOYEE_ID, COWORKER_ID, coworkerAuthorities);

        // Assert
        assertNotNull(result);
        assertEquals(EmployeeProfileDTO.class, result.getClass());

        verify(employeeMapper, times(1)).toEmployeePublicProfileDTO(targetEmployee);
        verify(absenceRepository, never()).findAllByEmployeeId(anyString());
        verify(feedbackRepository, never()).findAllByTargetEmployeeId(anyString());
        verify(employeeMapper, never()).toEmployeeSensitiveProfileDTO(any(), any(), any());
    }

    @Test
    void findById_EmployeeNotFound_ThrowsNotFoundException() {
        // Arrange
        var managerAuthorities = List.of(new SimpleGrantedAuthority("ROLE_" + EmployeeRole.MANAGER.name()));
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () ->
                employeeService.findById(EMPLOYEE_ID, MANAGER_ID, managerAuthorities)
        );
        verifyNoInteractions(absenceRepository, feedbackRepository, employeeMapper);
    }
}