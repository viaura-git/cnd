package com.fusionsoft.cnd.lea.member.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/members/v1")
public class HomeController {

    @GetMapping("/{id}")
    public String index(@PathVariable String id){
        log.debug("index started");
        log.debug("member id is {}", id);
        return "Member info for id = " + id;
    }
}
