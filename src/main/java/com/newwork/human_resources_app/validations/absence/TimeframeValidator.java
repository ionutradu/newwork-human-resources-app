package com.newwork.human_resources_app.validations.absence;

import com.newwork.human_resources_app.web.dto.Timeframe;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TimeframeValidator implements ConstraintValidator<ValidTimeframe, Timeframe> {

    @Override
    public void initialize(ValidTimeframe constraintAnnotation) {
    }

    @Override
    public boolean isValid(Timeframe dto, ConstraintValidatorContext context) {
        
        var startDate = dto.getStartDate();
        var endDate = dto.getEndDate();

        if (startDate == null || endDate == null) {
            return true;
        }

        return !endDate.isBefore(startDate);
    }
}