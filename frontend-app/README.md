# Frontend App - OAuth2 OIDC Spring Boot Application

A Spring Boot application demonstrating OAuth2 OIDC (OpenID Connect) Authorization Code Grant flow with a login page, home landing page, and calendar page.

## Features

- **OAuth2 OIDC Authorization Code Grant Flow**: Secure authentication using industry-standard OAuth2 OIDC
- **Login Page**: Custom login page with OAuth2 provider integration
- **Home Landing Page**: Secure home page accessible after authentication
- **Calendar Page**: Interactive calendar accessible from the home page
- **Session Management**: Automatic logout functionality

## Project Structure

```
frontend-app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/frontendapp/
│   │   │       ├── FrontendAppApplication.java
│   │   │       ├── controller/
│   │   │       │   └── AuthController.java
│   │   │       └── config/
│   │   │           └── SecurityConfig.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── templates/
│   │           ├── login.html
│   │           ├── home.html
│   │           └── calendar.html
│   └── test/
└── pom.xml
```

## Prerequisites

- Java 17 or later
- Maven 3.6+
- OAuth2 OIDC Provider (e.g., Google, GitHub, Keycloak)

## Setup Instructions

### 1. Configure OAuth2 OIDC Provider

Before running the application, you need to configure an OAuth2 provider. Example using Google:

#### Google OAuth2 Setup:
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project
3. Go to "Credentials" and create "OAuth 2.0 Client IDs" (Web application)
4. Add authorized redirect URI: `http://localhost:8080/login/oauth2/code/google`
5. Copy your Client ID and Client Secret

#### Keycloak Setup:
If using a self-hosted Keycloak instance:
1. Create a new realm
2. Create a new client with:
   - Client Protocol: openid-connect
   - Access Type: confidential
   - Valid Redirect URIs: `http://localhost:8080/*`
3. Go to "Credentials" tab and copy the client secret

### 2. Configure Application Properties

Edit `src/main/resources/application.yml` and add your OAuth2 provider credentials:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: YOUR_CLIENT_ID
            client-secret: YOUR_CLIENT_SECRET
            scope: openid,profile,email
```

Or for Keycloak:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          keycloak:
            client-id: YOUR_CLIENT_ID
            client-secret: YOUR_CLIENT_SECRET
            authorization-grant-type: authorization_code
            redirect-uri: http://localhost:8080/login/oauth2/code/keycloak
            scope: openid,profile,email
        provider:
          keycloak:
            issuer-uri: http://localhost:8080/realms/YOUR_REALM
            user-name-attribute: preferred_username
```

### 3. Build the Application

```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Application Flow

1. **Initial Access**: User visits `http://localhost:8080/`
   - Redirected to `/login` page

2. **Login Page**: User clicks OAuth2 provider button
   - Redirected to OAuth2 provider's authorization endpoint
   - User grants permission to the application
   - OAuth2 provider redirects back to the application with authorization code

3. **Authorization Code Exchange**: Spring Security
   - Exchanges authorization code for access token
   - Retrieves user information from OIDC provider
   - Creates authenticated session

4. **Home Page**: After successful authentication
   - User is redirected to `/home`
   - Displays welcome message and user information
   - Contains button to navigate to `/calendar`

5. **Calendar Page**: User can view an interactive calendar
   - Shows current month with navigation buttons
   - Highlights today's date
   - Back button to return to home page

6. **Logout**: User can logout from any page
   - Session is terminated
   - Redirected to login page

## Configuration Details

### SecurityConfig.java

The `SecurityConfig` class configures Spring Security with:
- OAuth2 login with custom login page
- Authorization requiring authentication for all endpoints except `/`, `/login`, and static resources
- Default success URL set to `/home`
- Logout functionality

### AuthController.java

Controllers for:
- `/` → Redirects to `/home`
- `/home` → Shows home landing page with user information
- `/calendar` → Shows interactive calendar
- `/login` → Custom login page

## Dependencies

- **spring-boot-starter-web**: Web support
- **spring-security-oauth2-client**: OAuth2 client support
- **spring-boot-starter-thymeleaf**: Server-side templating
- **thymeleaf-extras-springsecurity6**: Thymeleaf security tags

## Testing the Application

1. Start the application: `mvn spring-boot:run`
2. Navigate to `http://localhost:8080/`
3. Click "Sign in with [Provider]"
4. Complete OAuth2 authentication with your provider
5. You should be redirected to the home page with your user information
6. Click "Go to Calendar" button to view the calendar
7. Use "Logout" button to end the session

## Customization

### Adding Additional OAuth2 Providers

Add new provider registration to `application.yml`:

```yaml
registration:
  github:
    client-id: YOUR_GITHUB_CLIENT_ID
    client-secret: YOUR_GITHUB_CLIENT_SECRET
    scope: user:email
```

Update login.html to include additional provider button:

```html
<a href="/oauth2/authorization/github" class="oauth2-button github-button">
    Sign in with GitHub
</a>
```

### Styling

All pages include inline CSS that can be customized in the respective HTML template files:
- `templates/login.html` - Login page styling
- `templates/home.html` - Home page styling
- `templates/calendar.html` - Calendar page styling

## Security Notes

- Never commit credentials to version control
- Use environment variables or external configuration for sensitive data
- Always use HTTPS in production
- Validate and sanitize user input
- Keep Spring Security and dependencies updated

## Troubleshooting

### Redirect URI Mismatch
- Ensure the redirect URI in your OAuth2 provider configuration matches the one in `application.yml`
- Default format: `http://localhost:8080/login/oauth2/code/{provider}`

### Session Not Persisting
- Check that Spring Session is properly configured
- Verify cookies are enabled in the browser
- Check browser console for any JavaScript errors

### Calendar Not Loading
- Ensure JavaScript is enabled in your browser
- Check browser console for any errors
- Verify Thymeleaf templates are correctly placed in `src/main/resources/templates/`

## License

This project is provided as-is for educational purposes.
