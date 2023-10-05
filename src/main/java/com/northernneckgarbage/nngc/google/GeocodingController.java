package com.northernneckgarbage.nngc.google;

import com.google.maps.errors.ApiException;
import com.northernneckgarbage.nngc.dbConfig.RouteResponse;
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
private final RoutingService routingService;
    @GetMapping("/geocoding")
    public GeocodingData getGeocoding(@RequestParam String address) throws InterruptedException, ApiException, IOException {
        return geocodingService.getGeocoding(address);
    }
    @GetMapping("/geocoding/{id}")
    public ResponseEntity<GeocodingData> getGeocodingById(@PathVariable Long id) throws InterruptedException, ApiException, IOException {
        log.info("Geocoding id: "+id);

        return ResponseEntity.ok(geocodingService.getGeocodeByID(id));
    }

    @GetMapping("/google/create-route-4-driver/{pageNumber}")
            public ResponseEntity<RouteResponse> createRoute4Driver(@PathVariable int pageNumber) throws InterruptedException, ApiException, IOException{
            log.info("Creating route for driver");
            return ResponseEntity.ok(routingService.createRoute4OneDriver(pageNumber));

    }

    @GetMapping("/geocode_all")
    public ResponseEntity<?> geocodeAll() throws InterruptedException, ApiException, IOException {
        log.info("Geocoding all addresses");
        geocodingService.updateAllUsersGeocodes();
        return ResponseEntity.ok().build();
    }

}
