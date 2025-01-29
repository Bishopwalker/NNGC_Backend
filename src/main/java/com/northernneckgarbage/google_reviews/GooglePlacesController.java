package com.northernneckgarbage.google_reviews;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("nngc/googleReviews")
public class GooglePlacesController {

    private final GooglePlaceService googlePlacesService;

    @GetMapping("/getReviews")
    public String getReviews() {
        return googlePlacesService.getReviews();
    }

}
