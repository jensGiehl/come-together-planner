package de.agiehl.cometogether.controller;

import de.agiehl.cometogether.domain.model.AvailabilityStatus;
import de.agiehl.cometogether.domain.model.Survey;
import de.agiehl.cometogether.domain.model.User;
import de.agiehl.cometogether.service.SurveyService;
import de.agiehl.cometogether.service.UserService;
import de.agiehl.cometogether.service.VoteService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.Map;

@Controller
public class WebSocketController {

    private final VoteService voteService;
    private final UserService userService;
    private final SurveyService surveyService;

    public WebSocketController(VoteService voteService, UserService userService, SurveyService surveyService) {
        this.voteService = voteService;
        this.userService = userService;
        this.surveyService = surveyService;
    }

    @MessageMapping("/vote/{surveyPublicId}")
    @SendTo("/topic/survey/{surveyPublicId}")
    public Map<LocalDate, Map<AvailabilityStatus, Long>> handleVote(VotePayload payload, @DestinationVariable String surveyPublicId) {
        User user = userService.findByAccessCodeword(payload.getAccessCodeword()).orElseThrow(() -> new IllegalArgumentException("Invalid access codeword"));
        Survey survey = surveyService.getSurveyByPublicId(surveyPublicId).orElseThrow(() -> new IllegalArgumentException("Invalid survey public id"));

        voteService.castVote(survey, user, payload.getDate(), payload.getStatus());

        return voteService.getSurveyResults(survey.getId());
    }

    // Inner class for the payload
    public static class VotePayload {
        private String accessCodeword;
        private LocalDate date;
        private AvailabilityStatus status;

        // Getters and Setters
        public String getAccessCodeword() {
            return accessCodeword;
        }

        public void setAccessCodeword(String accessCodeword) {
            this.accessCodeword = accessCodeword;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public AvailabilityStatus getStatus() {
            return status;
        }

        public void setStatus(AvailabilityStatus status) {
            this.status = status;
        }
    }
}
