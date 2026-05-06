package com.yourpackage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private TokenService tokenService;

    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {

        boolean isValid = tokenService.validateToken(token, "admin");

        if (isValid) {
            return "admin/adminDashboard";
        }

        return "redirect:/";
    }

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {

        boolean isValid = tokenService.validateToken(token, "doctor");

        if (isValid) {
            return "doctor/doctorDashboard";
        }

        return "redirect:/";
    }
}