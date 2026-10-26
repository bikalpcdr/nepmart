package com.bikalpa.nepmart.core.homepage.controller;

import com.bikalpa.nepmart.annotation.SuccessMessage;
import com.bikalpa.nepmart.core.homepage.dto.HomepageResponseDto;
import com.bikalpa.nepmart.core.homepage.service.HomepageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/homepage")
@RequiredArgsConstructor
public class HomepageController {

    private final HomepageService homepageService;

    @GetMapping
    @SuccessMessage(action = "fetched", entity = "Homepage")
    public HomepageResponseDto getHomepage() {
        return homepageService.getHomepage();
    }
}
