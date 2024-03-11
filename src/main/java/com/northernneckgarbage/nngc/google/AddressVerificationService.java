package com.northernneckgarbage.nngc.google;

import com.google.maps.errors.ApiException;
import com.northernneckgarbage.nngc.google.GeocodingData;
import com.northernneckgarbage.nngc.google.GeocodingService;
import com.northernneckgarbage.nngc.google_routing.GoogleDistanceMatrixAPI;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
@Service
@RequiredArgsConstructor
public class AddressVerificationService {
    private final GeocodingService geocodingService;

    private static final List<String> INSIDE_COUNTIES = Arrays.asList("Northumberland County","Richmond", "Westmoreland County");
    private static final List<String> SURROUNDING_COUNTIES = Arrays.asList( "Essex County", "Lancaster County");

    public AddressStatus verifyAddress(String address) throws InterruptedException, ApiException, IOException {
        GeocodingData geocodingData = geocodingService.getGeocoding(address);
        String county = getCountyFromGeocodingData(geocodingData);

        if (INSIDE_COUNTIES.contains(county)) {
            return AddressStatus.INSIDE;
        } else if (SURROUNDING_COUNTIES.contains(county)) {
            return AddressStatus.SURROUNDING;
        } else {
            return AddressStatus.OUTSIDE;
        }
    }

    private String getCountyFromGeocodingData(GeocodingData geocodingData) {
        return geocodingData.getAddressComponents().stream()
                .filter(component -> component.getTypes().contains("ADMINISTRATIVE_AREA_LEVEL_2"))
                .map(com.northernneckgarbage.nngc.google.AddressComponent::getLongName) // Replaced method reference with lambda
                .findFirst()
                .orElse(null);
    }

    public enum AddressStatus {
        INSIDE, SURROUNDING, OUTSIDE
    }

    @Data
    static class AddressComponent {
        private String longName;
        private String shortName;
        private List<String> types;
    }
}
