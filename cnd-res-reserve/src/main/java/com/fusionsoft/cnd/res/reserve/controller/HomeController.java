package com.fusionsoft.cnd.res.reserve.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/reserves/v1")
public class HomeController {

    @GetMapping("/{id}")
    public String index(@PathVariable String id){
        log.debug("index started");
        log.debug("Reservation id is {}", id);
        return "Reservation id is " + id;
    }
}
