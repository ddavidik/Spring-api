package com.job.task.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    public ModelAndView showErrorPage(HttpServletRequest request) {
        int statusCode = (int) request.getAttribute("javax.servlet.error.status_code");
        String errorMessage = "";

        switch(statusCode) {
            case 400: {
                errorMessage = "Http Error Code: 400. Bad request. You probably tried to register an endpoint with an already saved name or URL.";
                break;
            }
            case 401: {
                errorMessage = "Http Error Code: 401. Unauthorized, please set the X-Authorization header.";
                break;
            }
            case 403: {
                errorMessage = "Http Error Code: 403. Forbidden, you don't have access to this resource.";
                break;
            }
            case 404: {
                errorMessage = "Http Error Code: 404. Not found.";
                break;
            }
            case 405: {
                errorMessage = "Http Error Code: 405. Method not allowed. ";
                break;
            }
            case 500: {
                errorMessage = "Http Error Code: 500. Interval server error.";
                break;
            }
            default: {
                errorMessage = "Error status: " + statusCode;
            }
        }

        return new ModelAndView("error", "errorMessage", errorMessage);
    }
}
