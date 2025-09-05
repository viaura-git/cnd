package com.fusionsoft.cnd.com.auth.domain.dto;

public record RegisterRequest(
        String userId, String userName, String password, String phone, String email
) {
}
