package com.calendar.frontendapp.security.oauth2.dpop;

import jakarta.annotation.PostConstruct;
import org.keycloak.common.crypto.CryptoIntegration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class DPoPConfig {

    private static final Logger logger = LoggerFactory.getLogger(DPoPConfig.class);

    // Static initializer to set properties as early as possible
    static {
        try {
            // Set Keycloak crypto provider to use Bouncy Castle
            // This MUST be done before any Keycloak classes are loaded
            System.setProperty("org.keycloak.client.crypto.provider", "BC");
            System.setProperty("org.keycloak.crypto.provider", "BC");

            logger.info("Keycloak crypto provider system properties set to BC (Bouncy Castle)");
        } catch (Exception ex) {
            logger.error("Failed to set crypto provider system properties", ex);
        }
    }

    @PostConstruct
    public void initializeCrypto() {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            // Initialize CryptoIntegration with the context classloader
            CryptoIntegration.init(classLoader);

            logger.info("Keycloak CryptoIntegration initialized successfully. Current provider: {}",
                    CryptoIntegration.getProvider().getClass().getName());
        } catch (Exception ex) {
            logger.error("Failed to initialize Keycloak CryptoIntegration", ex);
            throw new RuntimeException("Failed to initialize crypto provider", ex);
        }
    }

    @Bean
    public DPoPService dPoPService() {
        return new DPoPService();
    }
}
