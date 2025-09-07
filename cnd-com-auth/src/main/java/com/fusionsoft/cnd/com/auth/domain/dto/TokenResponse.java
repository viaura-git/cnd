package com.fusionsoft.cnd.com.auth.domain.dto;

import com.fusionsoft.cnd.com.auth.domain.type.AuthType;

// TokenResponse.java
public record TokenResponse(String accessToken, String refreshToken, AuthType authType) {}

