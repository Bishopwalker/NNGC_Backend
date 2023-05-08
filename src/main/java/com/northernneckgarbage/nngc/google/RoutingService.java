package com.northernneckgarbage.nngc.google;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoutingService {
    private final CustomerRepository customerRepository;
    private final GeocodingService geocodingService;

    public RoutingService(CustomerRepository customerRepository, GeocodingService geocodingService) {
        this.customerRepository = customerRepository;
        this.geocodingService = geocodingService;
    }

    public String createRoute4OneDriver() {
        GeoApiContext context = geocodingService.getContext();

        // Step 2: Fetch all customer addresses from the customer repository
        var users = customerRepository.findAll();

        // Step 3: Create a list of LatLng objects from the customer addresses
        List<LatLng> latLngs = users.stream().map(customer ->
                        new LatLng(customer.getLatitude(), customer.getLongitude()))
                .collect(Collectors.toList());

        // Steps 4-5: Set the first address as the origin, the last address as the destination, and the remaining addresses as waypoints
        LatLng origin = latLngs.get(0);
        LatLng destination = latLngs.get(latLngs.size() - 1);
        LatLng[] waypoints = latLngs.subList(1, latLngs.size() - 1).toArray(new LatLng[0]);

        try {
            // Step 6-7: Create a new Directions API request with the origin, destination, waypoints, and optimize the waypoints
            DirectionsResult result = DirectionsApi.newRequest(context)
                    .origin(origin)
                    .destination(destination)
                    .waypoints(waypoints)
                    .optimizeWaypoints(true)
                    .mode(TravelMode.DRIVING)
                    .await();

            // Step 8-9: Handle the response and print the route information
            if (result != null && result.routes != null && result.routes.length > 0) {
                DirectionsRoute route = result.routes[0];
                System.out.println("Total distance: " + route.legs[0].distance);
                System.out.println("Total duration: " + route.legs[0].duration);
                System.out.println("Route polyline: " + route.overviewPolyline);
            }
        } catch (ApiException | InterruptedException | IOException e) {
            // Handle exceptions
            e.printStackTrace();
        }
        return null;
    }
}
