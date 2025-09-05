package com.fusionsoft.cnd.com.auth.domain.type;

public enum RoleType {

    ROLE_ADMIN("관리자"),
    ROLE_DEVELOPER("개발자"),
    ROLE_USER("사용자");

    private String roleName;

    RoleType(String roleName) {
        this.roleName = roleName;
    }
}
