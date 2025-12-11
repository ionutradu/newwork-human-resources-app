package com.newwork.human_resources_app.domain.absence.web;

import com.newwork.human_resources_app.domain.absence.service.AbsenceService;
import com.newwork.human_resources_app.shared.dto.absence.AbsenceActionRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manager/absences")
@RequiredArgsConstructor
public class ManagerAbsenceController {

    private final AbsenceService absenceService;

    @PostMapping("/{requestId}/process")
    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void processAbsence(
            Authentication authentication,
            @PathVariable String requestId,
            @Valid @RequestBody AbsenceActionRequestDTO requestDTO) {

        var managerId = (String) authentication.getPrincipal();

        absenceService.processAbsenceRequest(requestId, managerId, requestDTO);
    }
}
