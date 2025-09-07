package com.fusionsoft.cnd.lea.member.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/members/v1")
public class HomeController {

    @GetMapping("/{id}")
    public ResponseEntity<?> index(
            @PathVariable String id,
            @RequestHeader("x-cnd-username") String username,
            @RequestHeader("x-cnd-roles") String rolesHeader) {

        log.debug("Member request for id={}, username={}, roles={}", id, username, rolesHeader);

        List<String> roles = Arrays.asList(rolesHeader.split(","));

        if (!roles.contains("ROLE_USER")) {
            return ResponseEntity.status(403).body("Forbidden: ROLE_USER required");
        }

        return ResponseEntity.ok("Member info for id=" + id + ", username=" + username);
    }
}
