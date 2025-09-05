package com.fusionsoft.cnd.com.auth.domain.dto;

import java.util.List;

public record UserInfoResponse(
        String userId,
        String username,
        String email,
        String phone,
        List<String> roles
) {}
