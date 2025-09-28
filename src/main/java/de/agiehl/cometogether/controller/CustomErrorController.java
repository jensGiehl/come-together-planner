package de.agiehl.cometogether.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        String errorMessage = "N/A";
        if (message != null && !message.toString().isEmpty()) {
            errorMessage = message.toString();
        }

        model.addAttribute("errorMessage", errorMessage);

        if (status != null) {
            model.addAttribute("statusCode", Integer.valueOf(status.toString()));
        } else {
            model.addAttribute("statusCode", "N/A");
        }

        return "error";
    }
}
