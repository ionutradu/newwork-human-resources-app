package com.newwork.human_resources_app.web.employees;

import com.newwork.human_resources_app.service.employee.EmployeeService;
import com.newwork.human_resources_app.service.employee.EmployeeActionsService;
import com.newwork.human_resources_app.service.mapper.EmployeeMapper;
import com.newwork.human_resources_app.web.dto.AbsenceRequestDTO;
import com.newwork.human_resources_app.web.dto.FeedbackRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeActionsController {

    private final EmployeeService employeeService;
    private final EmployeeActionsService employeeActionsService;
    private final EmployeeMapper employeeMapper;

    @PostMapping("/absence")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> requestAbsence(
            Authentication authentication,
            @RequestBody @Valid AbsenceRequestDTO dto) {
        var employeeId = (String) authentication.getPrincipal();

        employeeActionsService.requestAbsence(employeeId, dto);

        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{employeeId}/feedback")
    @PreAuthorize("hasAuthority('COWORKER')")
    public ResponseEntity<Void> leaveFeedback(
            Authentication authentication,
            @PathVariable("employeeId") String targetEmployeeId,
            @RequestBody @Valid FeedbackRequestDTO dto) {
        var employeeId = (String) authentication.getPrincipal();

        employeeActionsService.leaveFeedback(targetEmployeeId, employeeId, dto);

        return ResponseEntity.accepted().build();
    }
}
