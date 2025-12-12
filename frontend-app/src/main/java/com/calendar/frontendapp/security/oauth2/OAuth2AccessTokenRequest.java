package com.calendar.frontendapp.security.oauth2;

import org.springframework.util.LinkedMultiValueMap;

public class OAuth2AccessTokenRequest {

    org.springframework.util.LinkedMultiValueMap<String, String> formData = new org.springframework.util.LinkedMultiValueMap<>();

    public OAuth2AccessTokenRequest with(String name, String value) {
        this.formData.add(name, value);
        return this;
    }

    public LinkedMultiValueMap<String, String> getBody() {
        return formData;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("OAuth2AccessTokenRequest{");
        formData.forEach((key, values) -> {
            sb.append(key).append("=").append(values).append(", ");
        });
        if (!formData.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        return sb.append("}").toString();
    }
}
