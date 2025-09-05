package com.fusionsoft.cnd.com.auth.handler;

import com.fusionsoft.cnd.com.auth.domain.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        log.debug("=====> IllegalArgumentException - Global Exception Handler");
        ApiResponse<Void> response = ApiResponse.error("INVALID_ARGUMENT", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        // 예외 클래스 이름
        log.error("Exception caught: {} - {}", e.getClass().getSimpleName(), e.getMessage(), e);
        ApiResponse<Void> response = ApiResponse.error("INTERNAL_SERVER_ERROR", "예기치 못한 오류가 발생했습니다");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // 사용자 없을 때
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFound(UsernameNotFoundException e) {
        log.error("Exception caught: {} - {}", e.getClass().getSimpleName(), e.getMessage(), e);
        ApiResponse<Void> response = ApiResponse.error("USER_NOT_FOUND", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
