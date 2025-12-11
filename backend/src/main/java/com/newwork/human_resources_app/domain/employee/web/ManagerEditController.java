package com.newwork.human_resources_app.domain.employee.web;

import com.newwork.human_resources_app.domain.absence.repository.AbsenceRequest;
import com.newwork.human_resources_app.domain.employee.repository.Employee;
import com.newwork.human_resources_app.domain.employee.service.ManagerEditService;
import com.newwork.human_resources_app.domain.feedback.repository.Feedback;
import com.newwork.human_resources_app.shared.dto.manager.ManagerUpdateAbsenceRequestDTO;
import com.newwork.human_resources_app.shared.dto.manager.ManagerUpdateEmployeeDTO;
import com.newwork.human_resources_app.shared.dto.manager.ManagerUpdateFeedbackDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manager")
@PreAuthorize("hasAuthority('MANAGER')")
@RequiredArgsConstructor
public class ManagerEditController {

    private final ManagerEditService managerEditService;

    @PatchMapping("/employees/{id}")
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable String id, @RequestBody @Valid ManagerUpdateEmployeeDTO request) {
        var updatedEmployee = managerEditService.updateEmployee(id, request);
        return ResponseEntity.ok(updatedEmployee);
    }

    @PatchMapping("/absences/{id}")
    public ResponseEntity<AbsenceRequest> updateAbsenceRequest(
            @PathVariable String id,
            @RequestBody @Valid ManagerUpdateAbsenceRequestDTO request,
            Authentication authentication) {
        var managerId = (String) authentication.getPrincipal();

        var updatedAbsenceRequest = managerEditService.updateAbsenceRequest(id, request, managerId);
        return ResponseEntity.ok(updatedAbsenceRequest);
    }

    @PatchMapping("/feedbacks/{id}")
    public ResponseEntity<Feedback> updateFeedback(
            @PathVariable String id, @RequestBody @Valid ManagerUpdateFeedbackDTO request) {
        var updatedFeedback = managerEditService.updateFeedback(id, request);
        return ResponseEntity.ok(updatedFeedback);
    }
}
