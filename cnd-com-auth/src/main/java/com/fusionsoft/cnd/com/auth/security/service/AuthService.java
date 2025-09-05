package com.fusionsoft.cnd.com.auth.security.service;

import com.fusionsoft.cnd.com.auth.domain.dto.AuthRequest;
import com.fusionsoft.cnd.com.auth.domain.dto.RegisterRequest;
import com.fusionsoft.cnd.com.auth.domain.entity.User;
import com.fusionsoft.cnd.com.auth.domain.entity.Role;
import com.fusionsoft.cnd.com.auth.domain.type.RoleType;
import com.fusionsoft.cnd.com.auth.repository.RefreshTokenRepository;
import com.fusionsoft.cnd.com.auth.repository.UserRepository;
import com.fusionsoft.cnd.com.auth.security.model.CustomUserDetails;
import com.fusionsoft.cnd.com.auth.security.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    @Value("${jwt.refresh-token-validity-seconds}")
    private Long refreshSeconds;

    //Spring Security의 username은 한국의 userId 이다. 즉, "사용자로부터 입력받은 식별자"를 의미한다
    public User register(RegisterRequest req) {
        if (userRepository.existsByUserId(req.userId())) {
            throw new IllegalArgumentException("Username already exists");
        }
        User user = User.from(req, passwordEncoder);
        user.setRoles(
                new HashSet<>(Set.of(new Role(RoleType.ROLE_USER)))
                );
        return userRepository.save(user);
    }

    //username은 SpringSecurity 에서 "사용자를 구분하는 식별자" 의미이다. 구현할때 userId, email 등등 자유롭게 바꾼다
    public Map<String, String> login(AuthRequest req) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.userId(), req.password())
        );

        var principal = (CustomUserDetails) auth.getPrincipal();
        Set<Role> roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(roleStr -> new Role(RoleType.valueOf(roleStr))).collect(Collectors.toSet());

        String accessToken = jwtProvider.generateAccessToken(req.userId(), roles);
        String refreshToken = tokenService.generateAndStoreRefreshToken(req.userId());

        // save refresh token - RDB table 저장용. 이제 redis사용으로 하기는 주석
        /**
         * String refreshTokenStr = jwtProvider.generateRefreshToken(req.userId());
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenStr)
                .expiryDate(Instant.now().plusSeconds(refreshSeconds))
                .user(userRepository.findByUserId(req.userId()).orElseThrow())
                .build();

        refreshTokenRepository.save(refreshToken);
         **/


        Map<String,String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    public String refreshAccessToken(String refreshToken) {
        // 토큰에서 username 추출
        String username = jwtProvider.parseClaims(refreshToken).getSubject();

        // Redis에 있는 refreshToken 검증
        if (!tokenService.validateRefreshToken(username, refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        // User 엔티티 조회
        User user = userRepository.findByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Access Token 발급
        return jwtProvider.generateAccessToken(user.getUserId(), user.getRoles());
    }


    public void logout(String accessToken) {
        log.debug("AuthService logout start");
        String username = jwtProvider.parseClaims(accessToken).getSubject();
        log.debug("username: {}", username);

        // Refresh 제거
        tokenService.deleteRefreshToken(username);

        // Access 블랙리스트 처리
        String jti = jwtProvider.getJti(accessToken);
        long exp = jwtProvider.getExpiration(accessToken).getTime() - System.currentTimeMillis();
        if (exp > 0) {
            tokenService.blacklistAccessToken(jti, exp / 1000);
        }
    }
}

