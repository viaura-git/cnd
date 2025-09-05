package com.fusionsoft.cnd.com.auth.controller;

import com.fusionsoft.cnd.com.auth.domain.dto.ApiResponse;
import com.fusionsoft.cnd.com.auth.domain.dto.UserInfoResponse;
import com.fusionsoft.cnd.com.auth.domain.entity.User;
import com.fusionsoft.cnd.com.auth.repository.UserRepository;
import com.fusionsoft.cnd.com.auth.security.model.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final UserRepository userRepository;

    // === 로그인된 사용자가 자신의 정보를 가져오는 예제 ===
    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(@RequestHeader("x-cnd-username") String username,
                                       @RequestHeader("x-cnd-roles") String userRoles) {
        log.debug("=====> getMyInfo() start");

        log.debug("=====> user id : {}", username);
        log.debug("=====> user roles : {}", userRoles);

        User user = userRepository.findByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("등록되지 않은 사용자입니다"));

        log.debug("Myinfo from RDB is {}", user);

        UserInfoResponse response = new UserInfoResponse(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getPhone(),
                user.getRoles().stream()
                        .map(r -> r.getRoleName().name())
                        .collect(Collectors.toList())
        );

        // 예시로 username, roles만 반환
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
