package com.northernneckgarbage.nngc.controller;

import com.google.maps.errors.ApiException;
import com.northernneckgarbage.nngc.dbConfig.RouteResponse;
import com.northernneckgarbage.nngc.google.AddressVerificationService;
import com.northernneckgarbage.nngc.google.GeocodingData;
import com.northernneckgarbage.nngc.google.GeocodingService;
import com.northernneckgarbage.nngc.google.RoutingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/nngc")
@RequiredArgsConstructor
public class GeocodingController {

    private final GeocodingService geocodingService;
private final RoutingService routingService;

 private final AddressVerificationService addressVerificationService;

    @GetMapping("/geocoding")
    public GeocodingData getGeocoding(@RequestParam String address) throws InterruptedException, ApiException, IOException {
        return geocodingService.getGeocoding(address);
    }
    @GetMapping("/geocoding/{id}")
    public ResponseEntity<GeocodingData> getGeocodingById(@PathVariable Long id) throws InterruptedException, ApiException, IOException {
        log.info("Geocoding id: "+id);

        return ResponseEntity.ok(geocodingService.getGeocodeByID(id));
    }
    @GetMapping("/google/verify-address")
    public ResponseEntity<AddressVerificationService.AddressStatus> verifyAddress(@RequestParam String address) throws InterruptedException, ApiException, IOException {
        log.info("Verifying address: "+address);
        return ResponseEntity.ok(addressVerificationService.verifyAddress(address));
    }

    @GetMapping("/google/create-route-4-driver/{pageNumber}")
            public ResponseEntity<RouteResponse> createRoute4Driver(@PathVariable int pageNumber, @RequestParam Optional<String> county) throws InterruptedException, ApiException, IOException{
            log.info("Creating route for driver");
            return ResponseEntity.ok(routingService.createRoute4OneDriver(pageNumber, county));

    }

    @GetMapping("/geocode_all")
    public ResponseEntity<?> geocodeAll() throws InterruptedException, ApiException, IOException {
        log.info("Geocoding all addresses");
        geocodingService.updateAllUsersGeocodes();
        return ResponseEntity.ok().build();
    }

}
