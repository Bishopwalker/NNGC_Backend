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

    @GetMapping("/geocode_all")
    public ResponseEntity<GeocodingData> geocodeAll() throws InterruptedException, ApiException, IOException {
        log.info("Geocoding all addresses");
        return ResponseEntity.ok(geocodingService.updateAllUsersGeocodes());
    }

}
