package com.calendar.frontendapp.controller;

import com.calendar.frontendapp.security.oauth2.OAuth2AccessTokenResponse;
import com.calendar.frontendapp.security.oauth2.OAuth2Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class FrontendController {

    private static final Logger logger = LoggerFactory.getLogger(FrontendController.class);

    @Autowired
    private OAuth2Client oauth2Client;

    @GetMapping("/")
    public String index() {
        logger.debug("Received request for / - redirecting to /login");
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        model.addAttribute("message", "Welcome to Home");
        model.addAttribute("accessToken", (String) session.getAttribute("access_token"));
        return "home";
    }

    @GetMapping("/login")
    public String login(HttpSession session, Model model) {
        model.addAttribute("session", session);
        return "login";
    }

    @PostMapping("/oauth2/authorize")
    public String authorize(HttpSession session) {
        logger.info("Received POST request to /oauth2/authorize - initiating OAuth2 flow");
        try {
            String authorizationUrl = oauth2Client.authorizationUrl(session);
            if (authorizationUrl != null) {
                logger.info("Generated authorization URL, redirecting user");
                return "redirect:" + authorizationUrl;
            }
            logger.error("Failed to generate authorization URL");
            return "redirect:/login?error=authorization_failed";
        } catch (Exception ex) {
            logger.error("Authorization initiation failed: {}", ex.getMessage(), ex);
            return "redirect:/login?error=authorization_failed";
        }
    }

    @GetMapping("/oauth2/callback")
    public String handleAuthorizationCallback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "error", required = false) String error,
            HttpSession session,
            Model model) {
        if (error != null) {
            model.addAttribute("error", error);
            return "login";
        }

        if (code == null || code.isEmpty()) {
            model.addAttribute("error", "Missing authorization code");
            return "login";
        }

        if (state == null || state.isEmpty()) {
            model.addAttribute("error", "Missing state parameter");
            return "login";
        }

        try {
            OAuth2AccessTokenResponse tokenResponse = oauth2Client.tokenExchange(session, code);
            if (tokenResponse != null) {
                return "redirect:/home";
            } else {
                model.addAttribute("error", "Failed to obtain access token");
                return "login";
            }
        } catch (Exception ex) {
            model.addAttribute("error", "Token exchange failed: " + ex.getMessage());
            return "login";
        }
    }

}
