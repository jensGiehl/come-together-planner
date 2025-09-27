package de.agiehl.cometogether.controller;

import de.agiehl.cometogether.domain.model.Survey;
import de.agiehl.cometogether.domain.model.User;
import de.agiehl.cometogether.service.SurveyService;
import de.agiehl.cometogether.service.UserService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final SurveyService surveyService;

    public AdminController(UserService userService, SurveyService surveyService) {
        this.userService = userService;
        this.surveyService = surveyService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setAutoGrowCollectionLimit(256);
    }

    @GetMapping
    public String adminHome(Model model) {
        model.addAttribute("surveys", surveyService.getAllSurveys());
        return "admin/index";
    }

    // User management
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @GetMapping("/users/new")
    public String showCreateUserForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        model.addAttribute("suggestions", userService.suggestAccessCodewords());
        return "admin/user-form";
    }

    @PostMapping("/users")
    public String createUser(@Valid @ModelAttribute User user, BindingResult bindingResult, Model model) {
        System.out.println("DEBUG: createUser method hit for user: " + user.getName()); // Diagnostic print
        if (bindingResult.hasErrors()) {
            model.addAttribute("suggestions", userService.suggestAccessCodewords());
            return "admin/user-form";
        }
        try {
            userService.createUser(user.getName(), user.getAccessCodeword());
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("suggestions", userService.suggestAccessCodewords());
            return "admin/user-form";
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String showUpdateUserForm(@PathVariable Long id, Model model) {
        if (!model.containsAttribute("user")) {
            User user = userService.getUserById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
            model.addAttribute("user", user);
        }
        model.addAttribute("suggestions", userService.suggestAccessCodewords());
        return "admin/user-form";
    }

    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("suggestions", userService.suggestAccessCodewords());
            return "admin/user-form";
        }
        try {
            userService.updateUser(id, user.getName(), user.getAccessCodeword());
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("suggestions", userService.suggestAccessCodewords());
            return "admin/user-form";
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    // Survey management
    @GetMapping("/surveys")
    public String listSurveys(Model model) {
        model.addAttribute("surveys", surveyService.getAllSurveys());
        return "admin/index";
    }

    @GetMapping("/surveys/new")
    public String showCreateSurveyForm(Model model) { // Removed BindingResult
        if (!model.containsAttribute("survey")) {
            Survey survey = new Survey();
            survey.setPublicId(UUID.randomUUID().toString());
            survey.setWeekdays(new HashSet<>(Arrays.asList(DayOfWeek.values()))); // Default to all days
            model.addAttribute("survey", survey);
        }
        return "admin/survey-form";
    }

    @PostMapping("/surveys")
    public String createSurvey(@Valid @ModelAttribute Survey survey, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/survey-form";
        }

        if (survey.getExclusions() != null) {
            survey.getExclusions().removeIf(exclusion -> exclusion.getStartDate() == null);
            survey.getExclusions().forEach(exclusion -> exclusion.setSurvey(survey));
        }
        surveyService.createSurvey(survey);
        return "redirect:/admin/surveys";
    }

    @GetMapping("/surveys/edit/{id}")
    public String showUpdateSurveyForm(@PathVariable Long id, Model model) { // Removed BindingResult
        if (!model.containsAttribute("survey")) {
            Survey survey = surveyService.getSurveyById(id).orElseThrow(() -> new IllegalArgumentException("Invalid survey Id:" + id));
            model.addAttribute("survey", survey);
        }
        return "admin/survey-form";
    }

    @PostMapping("/surveys/update/{id}")
    public String updateSurvey(@PathVariable Long id, @Valid @ModelAttribute Survey survey, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/survey-form";
        }

        if (survey.getExclusions() != null) {
            survey.getExclusions().removeIf(ex -> ex.getStartDate() == null && ex.getId() == null);
            survey.getExclusions().forEach(exclusion -> exclusion.setSurvey(survey));
        }
        surveyService.updateSurvey(id, survey);
        return "redirect:/admin/surveys";
    }

    @PostMapping("/surveys/delete/{id}") // Added deleteSurvey method
    public String deleteSurvey(@PathVariable Long id) {
        surveyService.deleteSurvey(id);
        return "redirect:/admin/surveys";
    }
}
