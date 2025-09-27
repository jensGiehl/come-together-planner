package de.agiehl.cometogether.service;

import de.agiehl.cometogether.domain.model.AvailabilityStatus;
import de.agiehl.cometogether.domain.model.Survey;
import de.agiehl.cometogether.domain.model.User;
import de.agiehl.cometogether.domain.model.Vote;
import de.agiehl.cometogether.domain.repository.VoteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VoteService {

    private final VoteRepository voteRepository;

    public VoteService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    public List<Vote> getVotesForSurvey(Long surveyId) {
        return voteRepository.findBySurveyId(surveyId);
    }

    public Vote castVote(Survey survey, User user, LocalDate date, AvailabilityStatus status) {
        Vote vote = voteRepository.findBySurveyId(survey.getId()).stream()
                .filter(v -> v.getUser().equals(user) && v.getDate().equals(date))
                .findFirst()
                .orElse(new Vote());

        vote.setSurvey(survey);
        vote.setUser(user);
        vote.setDate(date);
        vote.setStatus(status);

        return voteRepository.save(vote);
    }

    public Map<LocalDate, Map<AvailabilityStatus, Long>> getSurveyResults(Long surveyId) {
        return voteRepository.findBySurveyId(surveyId).stream()
                .collect(Collectors.groupingBy(Vote::getDate,
                        Collectors.groupingBy(Vote::getStatus, Collectors.counting())));
    }

    public Map<LocalDate, AvailabilityStatus> getUserVotesForSurvey(Long userId, Long surveyId) {
        return voteRepository.findByUserIdAndSurveyId(userId, surveyId).stream()
                .collect(Collectors.toMap(Vote::getDate, Vote::getStatus));
    }
}
