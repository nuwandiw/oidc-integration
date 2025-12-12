package com.calendar.frontendapp.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet-based filter for extracting and validating OAuth2 access tokens from the session.
 * If an access token is found in the session, it creates an OAuth2AuthenticationToken
 * and establishes it in the SecurityContext for the request.
 */
public class SessionAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SessionAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();

        // Skip filtering for public endpoints
        if (path.equals("/") || path.equals("/login") || path.startsWith("/oauth2/callback") || path.equals("/oauth2/authorize")) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            logger.debug("No session found for request to {}", path);
            response.sendRedirect("/login");
            return;
        }

        String accessToken = (String) session.getAttribute("access_token");
        String tokenType = (String) session.getAttribute("token_type");
        String username = (String) session.getAttribute("username");

        if (accessToken != null && !accessToken.isEmpty()) {
            logger.debug("Found access token in session for user: {}", username != null ? username : "unknown");
            OAuth2AuthenticationToken authToken = new OAuth2AuthenticationToken(
                    username != null ? username : "anonymous-user",
                    accessToken,
                    tokenType != null ? tokenType : "Bearer",
                    java.util.Collections.emptyList()
            );

            SecurityContext securityContext = new SecurityContextImpl(authToken);
            SecurityContextHolder.setContext(securityContext);
            logger.debug("Session-based authentication established for user: {}", authToken.getName());

            filterChain.doFilter(request, response);
        } else {
            logger.info("No access token found in session");
            response.sendRedirect("/login");
        }
    }
}
