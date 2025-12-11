package com.newwork.human_resources_app.domain.employee.web;

import com.newwork.human_resources_app.domain.absence.service.AbsenceService;
import com.newwork.human_resources_app.domain.feedback.service.FeedbackService;
import com.newwork.human_resources_app.shared.dto.FeedbackRequestDTO;
import com.newwork.human_resources_app.shared.dto.absence.AbsenceRequestDTO;
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

    private final FeedbackService feedbackService;
    private final AbsenceService absenceService;

    @PostMapping("/absence")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> requestAbsence(
            Authentication authentication, @RequestBody @Valid AbsenceRequestDTO dto) {
        var employeeId = (String) authentication.getPrincipal();

        absenceService.requestAbsence(employeeId, dto);

        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{employeeId}/feedback")
    @PreAuthorize("hasAuthority('COWORKER')")
    public ResponseEntity<Void> leaveFeedback(
            Authentication authentication,
            @PathVariable("employeeId") String targetEmployeeId,
            @RequestBody @Valid FeedbackRequestDTO dto) {
        var employeeId = (String) authentication.getPrincipal();

        feedbackService.leaveFeedback(targetEmployeeId, employeeId, dto);

        return ResponseEntity.accepted().build();
    }
}
