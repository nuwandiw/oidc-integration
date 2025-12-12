package com.calendar.frontendapp.security.oauth2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OAuth2ClientConfig {

    @Value("${spring.oauth2.client.id}")
    private String clientId;

    @Value("${spring.oauth2.client.redirect-uri}")
    private String redirectUri;

    @Value("${spring.oauth2.client.scope}")
    private String scope;

    @Value("${spring.oauth2.client.authorization-uri}")
    private String authorizationUri;

    @Value("${spring.oauth2.client.token-uri}")
    private String tokenUri;

    @Value("${spring.oauth2.client.secret}")
    private String clientSecret;

    /**
     * Creates a RestTemplate bean for making HTTP requests to the authorization server.
     * RestTemplate is a Spring synchronous HTTP client for making REST calls.
     *
     * @param builder the RestTemplateBuilder for customizing the RestTemplate
     * @return a configured RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    /**
     * Creates OAuth2Properties from configuration values.
     *
     * @return OAuth2Properties instance with all configured values
     */
    private OAuth2Properties oAuth2Properties() {
        return OAuth2Properties.builder()
                .clientId(clientId)
                .redirectUri(redirectUri)
                .scope(scope)
                .authorizationUri(authorizationUri)
                .tokenUri(tokenUri)
                .clientSecret(clientSecret)
                .build();
    }

    /**
     * Creates the OAuth2Client bean with RestTemplate.
     *
     * @return OAuth2Client configured with properties and RestTemplate
     */
    @Bean
    public OAuth2Client oAuth2Client(RestTemplate restTemplate) {
        return new OAuth2Client(oAuth2Properties(), restTemplate);
    }
}
