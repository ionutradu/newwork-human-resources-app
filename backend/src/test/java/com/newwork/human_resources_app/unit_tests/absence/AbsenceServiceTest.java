package com.newwork.human_resources_app.unit_tests.absence;

import com.newwork.human_resources_app.domain.absence.repository.AbsenceRepository;
import com.newwork.human_resources_app.domain.absence.repository.AbsenceRequest;
import com.newwork.human_resources_app.domain.absence.service.AbsenceService;
import com.newwork.human_resources_app.domain.employee.repository.Employee;
import com.newwork.human_resources_app.domain.employee.repository.EmployeeRepository;
import com.newwork.human_resources_app.event.kafka.KafkaEventProducerService;
import com.newwork.human_resources_app.shared.dto.absence.AbsenceRequestDTO;
import com.newwork.human_resources_app.shared.exception.NotFoundException;
import com.newwork.human_resources_app.shared.mapper.AbsenceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbsenceServiceTest {

    @Mock
    private AbsenceRepository absenceRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private AbsenceMapper absenceMapper;
    @Mock
    private KafkaEventProducerService kafkaEventProducerService;

    @InjectMocks
    private AbsenceService absenceService;

    private final String EMPLOYEE_ID = "emp123";
    private AbsenceRequestDTO requestDTO;
    private Employee employee;
    private AbsenceRequest absenceRequest;

    @BeforeEach
    void setUp() {
        requestDTO = new AbsenceRequestDTO(
            LocalDate.of(2026, 1, 1), 
            LocalDate.of(2026, 1, 5), 
            "Vacation"
        );
        
        employee = new Employee();
        employee.setId(EMPLOYEE_ID); 
        
        absenceRequest = new AbsenceRequest();
        absenceRequest.setEmployeeId(EMPLOYEE_ID);
    }

    @Test
    void createAbsenceRequest_Success() {
        when(absenceMapper.toEntity(any(AbsenceRequestDTO.class), eq(EMPLOYEE_ID))).thenReturn(absenceRequest);
        when(absenceRepository.save(any(AbsenceRequest.class))).thenReturn(absenceRequest);

        absenceService.requestAbsence(EMPLOYEE_ID, requestDTO);

        verify(absenceRepository, times(1)).save(absenceRequest);
        verify(kafkaEventProducerService, times(1)).sendAbsenceCreatedEvent(EMPLOYEE_ID, absenceRequest.getId());
    }

}