package com.northernneckgarbage.google_reviews;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class GooglePlaceService {
    Dotenv dotenv = Dotenv.load();

    @Value("${spring.google.placeID}")
 private String placeID;

    private final String API_KEY = dotenv.get("GOOGLE_MAPS_API_KEY");


    private final RestTemplate restTemplate = new RestTemplate();


    public String getReviews() {
        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeID + "&key=" + API_KEY;
        return restTemplate.getForObject(url, String.class);
    }


}
