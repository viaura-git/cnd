package com.fusionsoft.cnd.com.auth.controller;

import com.fusionsoft.cnd.com.auth.domain.dto.*;
import com.fusionsoft.cnd.com.auth.domain.type.AuthType;
import com.fusionsoft.cnd.com.auth.security.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req){
        var user = authService.register(req);
        return ResponseEntity.ok(Map.of("userID", user.getUserId(), "fullName", user.getUserName()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req){
        log.info("===> start login");

        var tokens = authService.login(req);
        log.debug("=====> tokens: {}", tokens);

        TokenResponse tokenResponse = new TokenResponse(
                tokens.get("accessToken"),
                tokens.get("refreshToken"),
                AuthType.BEARER
        );

        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody TokenRequest request){
        log.debug("new access token publish start : /refresh");

        String refreshToken = request.refreshToken();
        log.debug("refreshToken: {}", refreshToken);

        String newAccessToken = authService.refreshAccessToken(refreshToken);
        log.debug("newAccessToken: {}", newAccessToken);

        TokenResponse tokenResponse = new TokenResponse(newAccessToken, null, AuthType.BEARER);

        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        log.debug("logout start");
        // Authorization 헤더에서 Bearer 토큰 추출
        String header = request.getHeader("Authorization");
        log.debug("header : {}", header);
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String accessToken = header.substring(7);
            log.debug("accessToken : {}", accessToken);
            authService.logout(accessToken); // Service에서 username 추출 + Redis refresh 삭제 + Access 블랙리스트
        }
        return ResponseEntity.noContent().build();
    }
}
