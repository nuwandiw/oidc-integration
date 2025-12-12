package com.calendar.frontendapp.security.oauth2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;

import static com.calendar.frontendapp.security.oauth2.OAuthUtil.buildAuthorizationUrl;
import static com.calendar.frontendapp.security.oauth2.OAuthUtil.generateCodeChallenge;
import static com.calendar.frontendapp.security.oauth2.OAuthUtil.generateCodeVerifier;
import static com.calendar.frontendapp.security.oauth2.OAuthUtil.generateState;

public class OAuth2Client {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2Client.class);

    private OAuth2Properties properties;
    private RestTemplate restTemplate;

    public OAuth2Client(OAuth2Properties properties, RestTemplate restTemplate) {
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    public String authorizationUrl(HttpSession session) {
        String state = generateState();
        String codeVerifier = generateCodeVerifier();
        String codeChallenge = generateCodeChallenge(codeVerifier);

        // Store state and code_verifier in session for later validation
        session.setAttribute("oauth_state", state);
        session.setAttribute("code_verifier", codeVerifier);

        return buildAuthorizationUrl(
                properties.getAuthorizationUri(),
                properties.getClientId(),
                properties.getRedirectUri(),
                properties.getScope(),
                state,
                codeChallenge
        );
    }

    public OAuth2AccessTokenResponse tokenExchange(HttpSession session, String authorizationCode) {
        String codeVerifier = (String) session.getAttribute("code_verifier");
        if (codeVerifier == null) {
            throw new IllegalArgumentException("code_verifier not found in session");
        }

        // Build form data for the token request
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", authorizationCode);
        formData.add("client_id", properties.getClientId());
        if (properties.getClientSecret() != null) {
            formData.add("client_secret", properties.getClientSecret());
        }
        formData.add("redirect_uri", properties.getRedirectUri());
        formData.add("code_verifier", codeVerifier);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        logger.info("Sending token exchange request to client {} to {}", properties.getClientId(), properties.getTokenUri());

        try {
            OAuth2AccessTokenResponse tokenResponse = restTemplate.postForObject(
                    properties.getTokenUri(),
                    requestEntity,
                    OAuth2AccessTokenResponse.class
            );

            if (tokenResponse != null) {
                session.setAttribute("access_token", tokenResponse.getAccessToken());
                session.setAttribute("token_type", tokenResponse.getTokenType());
                session.setAttribute("expires_in", tokenResponse.getExpiresIn());
                return tokenResponse;
            } else {
                logger.error("Token response is null from authorization server");
                throw new RuntimeException("Failed to obtain access token from authorization server");
            }
        } catch (Exception ex) {
            logger.error("Token exchange failed: {}", ex.getMessage(), ex);
            throw new RuntimeException("Token exchange failed: " + ex.getMessage(), ex);
        }
    }
}
