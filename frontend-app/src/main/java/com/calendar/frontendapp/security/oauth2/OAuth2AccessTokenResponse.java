package com.calendar.frontendapp.security.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an OAuth 2.0 DPoP Access Token Response as per RFC 9449 and RFC 6749.
 *
 * This class models the response returned by the authorization server's token endpoint
 * when an access token is successfully issued and bound to a DPoP proof.
 *
 * Reference: https://datatracker.ietf.org/doc/html/rfc9449#section-5
 *            https://datatracker.ietf.org/doc/html/rfc6749#section-5.1
 */
public class OAuth2AccessTokenResponse {

    /**
     * REQUIRED. The access token issued by the authorization server.
     * For DPoP-bound tokens, this token is bound to the client's public key.
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * REQUIRED. The type of the token issued.
     * Value MUST be "DPoP" for DPoP-bound tokens, or "Bearer" for standard bearer tokens.
     * Case insensitive per RFC 6749.
     */
    @JsonProperty("token_type")
    private String tokenType;

    /**
     * RECOMMENDED. The lifetime in seconds of the access token.
     * For example, 3600 means the access token expires in one hour from when the response
     * was generated.
     */
    @JsonProperty("expires_in")
    private Long expiresIn;

    /**
     * OPTIONAL. The refresh token, which can be used to obtain new access tokens.
     * Included only if the authorization server issued one and the client is entitled to one.
     * For public clients, refresh tokens must also be DPoP-bound when DPoP is used.
     */
    @JsonProperty("refresh_token")
    private String refreshToken;

    /**
     * OPTIONAL. The scope of the access token.
     * REQUIRED if it differs from the scope requested by the client.
     */
    @JsonProperty("scope")
    private String scope;

    /**
     * OPTIONAL. Additional parameters that may be returned by the authorization server.
     * This can include implementation-specific or extension parameters.
     */
    @JsonProperty("custom_parameters")
    private java.util.Map<String, Object> customParameters;

    // Constructors

    public OAuth2AccessTokenResponse() {
    }

    public OAuth2AccessTokenResponse(String accessToken, String tokenType) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
    }

    public OAuth2AccessTokenResponse(String accessToken, String tokenType, Long expiresIn) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }

    // Getters and Setters

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public java.util.Map<String, Object> getCustomParameters() {
        return customParameters;
    }

    public void setCustomParameters(java.util.Map<String, Object> customParameters) {
        this.customParameters = customParameters;
    }

    /**
     * Checks if this is a DPoP-bound token response.
     *
     * @return true if the token_type is "DPoP", false otherwise
     */
    public boolean isDPopBound() {
        return tokenType != null && tokenType.equalsIgnoreCase("DPoP");
    }

    /**
     * Checks if the access token has expired based on the issued time and expires_in value.
     *
     * @param issuedAtMillis The time (in milliseconds) when the token was issued
     * @return true if the token has expired, false otherwise
     */
    public boolean isExpired(long issuedAtMillis) {
        if (expiresIn == null) {
            return false; // No expiration info available
        }
        long expirationTimeMillis = issuedAtMillis + (expiresIn * 1000);
        return System.currentTimeMillis() > expirationTimeMillis;
    }

    @Override
    public String toString() {
        return "OAuth2AccessTokenResponse{" +
                "accessToken='" + (accessToken != null ? "***" : null) + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", refreshToken='" + (refreshToken != null ? "***" : null) + '\'' +
                ", scope='" + scope + '\'' +
                ", customParameters=" + customParameters +
                '}';
    }
}
