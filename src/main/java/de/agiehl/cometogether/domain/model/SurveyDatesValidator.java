package de.agiehl.cometogether.domain.model;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SurveyDatesValidator implements ConstraintValidator<ValidSurveyDates, Survey> {

    @Override
    public boolean isValid(Survey survey, ConstraintValidatorContext context) {
        if (survey.getStartDate() == null || survey.getEndDate() == null) {
            return true; // Let other validators handle null dates
        }
        return survey.getEndDate().isAfter(survey.getStartDate());
    }
}
