package com.newwork.human_resources_app.web.absences;

import com.newwork.human_resources_app.service.employee.EmployeeActionsService;
import com.newwork.human_resources_app.web.dto.AbsenceActionRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manager/absences")
@RequiredArgsConstructor
public class ManagerAbsenceController {

    private final EmployeeActionsService employeeActionsService;

    @PostMapping("/{requestId}/process")
    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void processAbsence(
            Authentication authentication,
            @PathVariable String requestId,
            @Valid @RequestBody AbsenceActionRequestDTO requestDTO) {

        var managerId = (String) authentication.getPrincipal();

        employeeActionsService.processAbsenceRequest(requestId, managerId, requestDTO);
    }
}
