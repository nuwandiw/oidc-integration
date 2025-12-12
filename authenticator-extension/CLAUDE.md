# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Keycloak custom authenticator extension that implements a secret question-based authentication mechanism. It allows users to set a secret question/answer during their first login to a new machine, and they will be prompted with it on subsequent logins from new devices. A cookie-based mechanism prevents repeated prompts within a configurable time window (default: 30 days).

## Build and Development Commands

### Build the Extension
```bash
mvn -Pextension clean install -DskipTests=true
```

### Build with Tests
```bash
mvn -Pextension clean install
```

### Run Integration Tests Only
```bash
mvn -Pextension verify
```

### Run a Single Test
```bash
mvn -Pextension -Dtest=TestClassName -DfailIfNoTests=false test
```

### Skip Tests During Build
Already included in the main build command above.

## Architecture and Code Structure

### Core SPI Implementations

The extension implements three main Keycloak SPI (Service Provider Interface) components:

1. **Authenticator SPI** (`SecretQuestionAuthenticator` and `SecretQuestionAuthenticatorFactory`)
   - Handles the authentication flow during user login
   - Checks for the `SECRET_QUESTION_ANSWERED` cookie to bypass the challenge if recently authenticated
   - Presents the secret question form if cookie is not present
   - Validates the user's answer and sets the cookie on success
   - Configuration property: `cookie.max.age` (in seconds)

2. **Credential Provider SPI** (`SecretQuestionCredentialProvider` and `SecretQuestionCredentialProviderFactory`)
   - Manages storage and validation of secret question credentials
   - Validates user input by comparing the provided answer against the stored credential
   - Uses Keycloak's credential manager API for storage/retrieval
   - Implements `CredentialInputValidator` for answer validation

3. **Required Action SPI** (`SecretQuestionRequiredAction` and `SecretQuestionRequiredActionFactory`)
   - Triggered when a user logs in for the first time or lacks the credential
   - Presents the configuration form for users to set their secret answer
   - Currently hardcoded question: "What is your mom's first name?" (defined in `SecretQuestionCredentialModel.createSecretQuestion()`)

### Data Models

- **`SecretQuestionCredentialModel`** (`src/main/java/org/keycloak/examples/authenticator/credential/SecretQuestionCredentialModel.java`)
  - Wraps credential data with question and answer information
  - Converts to/from Keycloak's `CredentialModel` format

- **`SecretQuestionCredentialData`** and **`SecretQuestionSecretData`** (DTOs)
  - `SecretQuestionCredentialData`: Stores the question
  - `SecretQuestionSecretData`: Stores the answer

### Service Registration

SPI implementations are registered via Java service loader mechanism in `src/main/resources/META-INF/services/`:
- `org.keycloak.authentication.AuthenticatorFactory` → `SecretQuestionAuthenticatorFactory`
- `org.keycloak.credential.CredentialProviderFactory` → `SecretQuestionCredentialProviderFactory`
- `org.keycloak.authentication.RequiredActionFactory` → `SecretQuestionRequiredActionFactory`

### Templates

Freemarker templates in `src/main/resources/theme-resources/templates/`:
- `secret-question.ftl` - Authentication challenge form (displays question, prompts for answer)
- `secret-question-config.ftl` - Required action form (allows user to set their answer)

## Deployment

1. Build the JAR: `mvn -Pextension clean install -DskipTests=true`
2. Copy `target/authenticator-example.jar` to Keycloak's `providers` directory
3. Start/restart Keycloak: `kc.sh start-dev` (or `kc.bat` on Windows)
4. Register the authenticator in your realm's authentication flow via Admin Console
5. Enable the `Secret Question` required action in the realm

## Key Technical Details

### Dependencies
- Keycloak Server SPI (v26.4.7)
- Jakarta JAX-RS for REST/HTTP handling
- JBoss Logging for logging

### Java Version
- Requires Java 17 or later (set in pom.xml: `maven.compiler.source` and `maven.compiler.target`)

### Important Code Locations
- Main authenticator logic: `SecretQuestionAuthenticator:57-79` (authenticate and action methods)
- Cookie handling: `SecretQuestionAuthenticator:47-96`
- Credential validation: `SecretQuestionCredentialProvider:47-62`
- Answer registration: `SecretQuestionRequiredAction:55-60`

### Cookie Security Notes
- Cookie is set with `secure=false` (line 93 in `SecretQuestionAuthenticator`) - should be `true` in production
- Uses path-based scoping to the realm
- Configurable max age (default 30 days, controlled by `cookie.max.age` property)

