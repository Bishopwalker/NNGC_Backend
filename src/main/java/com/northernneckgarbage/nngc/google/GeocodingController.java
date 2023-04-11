package com.northernneckgarbage.nngc.google;

import com.google.maps.errors.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/nngc")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class GeocodingController {

    private final GeocodingService geocodingService;

    @GetMapping("/geocoding")
    public GeocodingData getGeocoding(@RequestParam String address) throws InterruptedException, ApiException, IOException {
        return geocodingService.getGeocoding(address);
    }
    @GetMapping("/geocoding/{id}")
    public ResponseEntity<GeocodingData> getGeocodingById(@PathVariable Long id) throws InterruptedException, ApiException, IOException {
        log.info("Geocoding id: "+id);

        return ResponseEntity.ok(geocodingService.getGeocodeByID(id));
    }

}
