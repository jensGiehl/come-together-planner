package de.agiehl.cometogether.service;

import de.agiehl.cometogether.domain.model.DateExclusion;
import de.agiehl.cometogether.domain.model.Survey;
import de.agiehl.cometogether.domain.repository.SurveyRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SurveyService {

    private final SurveyRepository surveyRepository;

    public SurveyService(SurveyRepository surveyRepository) {
        this.surveyRepository = surveyRepository;
    }

    public List<Survey> getAllSurveys() {
        return surveyRepository.findAll();
    }

    public Optional<Survey> getSurveyById(Long id) {
        return surveyRepository.findById(id);
    }

    public Optional<Survey> getSurveyByPublicId(String publicId) {
        return surveyRepository.findByPublicId(publicId);
    }

    public Survey createSurvey(Survey survey) {
        survey.setPublicId(UUID.randomUUID().toString());
        return surveyRepository.save(survey);
    }

    public Survey updateSurvey(Long id, Survey surveyDetails) {
        Survey survey = surveyRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid survey Id:" + id));
        survey.setTitle(surveyDetails.getTitle());
        survey.setDescription(surveyDetails.getDescription());
        survey.setStartDate(surveyDetails.getStartDate());
        survey.setEndDate(surveyDetails.getEndDate());
        survey.setWeekdays(surveyDetails.getWeekdays());
        survey.getExclusions().clear();
        survey.getExclusions().addAll(surveyDetails.getExclusions());
        return surveyRepository.save(survey);
    }

    public void deleteSurvey(Long id) {
        surveyRepository.deleteById(id);
    }

    public List<LocalDate> getAvailableDates(Survey survey) {
        Set<DayOfWeek> allowedWeekdays = survey.getWeekdays();
        if (allowedWeekdays == null || allowedWeekdays.isEmpty()) {
            allowedWeekdays = Set.of(DayOfWeek.values());
        }
        final Set<DayOfWeek> finalAllowedWeekdays = allowedWeekdays;
        final List<DateExclusion> exclusions = survey.getExclusions();

        return survey.getStartDate().datesUntil(survey.getEndDate().plusDays(1))
                .filter(date -> finalAllowedWeekdays.contains(date.getDayOfWeek()))
                .filter(date -> exclusions.stream().noneMatch(ex -> !date.isBefore(ex.getStartDate()) && !date.isAfter(ex.getEndDate())))
                .collect(Collectors.toList());
    }
}
