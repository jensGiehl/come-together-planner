package de.agiehl.cometogether.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/find-survey")
    public String redirectToSurvey(@RequestParam("surveyCode") String surveyCode) {
        return "redirect:/survey/" + surveyCode;
    }

}
