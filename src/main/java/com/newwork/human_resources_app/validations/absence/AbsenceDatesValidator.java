package com.newwork.human_resources_app.validations.absence;

import com.newwork.human_resources_app.web.dto.AbsenceRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class AbsenceDatesValidator implements ConstraintValidator<ValidAbsenceDates, AbsenceRequestDTO> {

    @Override
    public void initialize(ValidAbsenceDates constraintAnnotation) {
    }

    @Override
    public boolean isValid(AbsenceRequestDTO dto, ConstraintValidatorContext context) {
        
        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();

        if (startDate == null || endDate == null) {
            return true;
        }

        return !endDate.isBefore(startDate);
    }
}