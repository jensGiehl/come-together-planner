package de.agiehl.cometogether.controller;

import de.agiehl.cometogether.domain.model.AvailabilityStatus;
import de.agiehl.cometogether.domain.model.Survey;
import de.agiehl.cometogether.domain.model.User;
import de.agiehl.cometogether.service.SurveyService;
import de.agiehl.cometogether.service.UserService;
import de.agiehl.cometogether.service.VoteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/survey")
public class SurveyController {

    private final SurveyService surveyService;
    private final VoteService voteService;
    private final UserService userService;

    // View-Model to hold all data for a single row in the template
    public record SurveyDateView(LocalDate date, String formattedDate, AvailabilityStatus userVote, long available, long maybe, long unavailable, long total) {}

    public SurveyController(SurveyService surveyService, VoteService voteService, UserService userService) {
        this.surveyService = surveyService;
        this.voteService = voteService;
        this.userService = userService;
    }

    @GetMapping("/{publicId}")
    public String viewSurvey(@PathVariable String publicId, Model model, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        Optional<Survey> surveyOpt = surveyService.getSurveyByPublicId(publicId);

        if (surveyOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Die gesuchte Umfrage existiert nicht.");
            return "redirect:/";
        }

        Survey survey = surveyOpt.get();

        // Determine user and their votes
        Map<LocalDate, AvailabilityStatus> userVotes = Collections.emptyMap();
        String userName = "Gast";
        if (userDetails != null) {
            if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                userName = "Admin";
            } else {
                User user = userService.findByAccessCodeword(userDetails.getUsername())
                        .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + userDetails.getUsername()));
                userName = user.getName();
                Map<LocalDate, AvailabilityStatus> votes = voteService.getUserVotesForSurvey(user.getId(), survey.getId());
                if (votes != null) {
                    userVotes = votes;
                }
            }
        }

        // Prepare the data for the view
        Map<LocalDate, Map<AvailabilityStatus, Long>> allResults = voteService.getSurveyResults(survey.getId());
        List<LocalDate> availableDates = surveyService.getAvailableDates(survey);
        final Map<LocalDate, AvailabilityStatus> finalUserVotes = userVotes; // Final for use in lambda

        List<SurveyDateView> dateViews = availableDates.stream().map(date -> {
            Map<AvailabilityStatus, Long> resultsForDate = allResults.getOrDefault(date, Collections.emptyMap());
            long available = resultsForDate.getOrDefault(AvailabilityStatus.AVAILABLE, 0L);
            long maybe = resultsForDate.getOrDefault(AvailabilityStatus.MAYBE, 0L);
            long unavailable = resultsForDate.getOrDefault(AvailabilityStatus.UNAVAILABLE, 0L);
            long total = available + maybe + unavailable;
            String formattedDate = date.format(DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy", Locale.GERMAN));

            return new SurveyDateView(date, formattedDate, finalUserVotes.get(date), available, maybe, unavailable, total);
        }).collect(Collectors.toList());

        // Add the prepared data to the model
        model.addAttribute("survey", survey);
        model.addAttribute("dateViews", dateViews);
        model.addAttribute("userName", userName);
        model.addAttribute("results", allResults); // Still needed for JS top-dates

        return "survey";
    }
}
