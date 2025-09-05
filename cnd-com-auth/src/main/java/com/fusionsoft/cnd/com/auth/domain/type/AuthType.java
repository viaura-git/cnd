package com.fusionsoft.cnd.com.auth.domain.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AuthType {
    BEARER;

    @JsonValue
    @Override
    public String toString() {
        return "Bearer";
    }
}
