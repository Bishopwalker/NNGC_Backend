package com.northernneckgarbage.nngc.google;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import com.northernneckgarbage.nngc.dbConfig.RouteResponse;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoutingService {
    private final CustomerRepository customerRepository;
    private final GeocodingService geocodingService;



    public RouteResponse createRoute4OneDriver() {
        GeoApiContext context = geocodingService.getContext();

        // Step 2: Fetch all customer addresses from the customer repository
        var users = customerRepository.findAll();

        // Step 3: Create a list of LatLng objects from the customer addresses
        List<LatLng> latLngs = users.stream().map(customer ->
                        new LatLng(customer.getLatitude(), customer.getLongitude()))
                .toList();

        // Steps 4-5: Set the first address as the origin, the last address as the destination, and the remaining addresses as waypoints
        LatLng origin = latLngs.get(0);
        LatLng destination = latLngs.get(latLngs.size() - 1);
        LatLng[] waypoints = latLngs.subList(1, latLngs.size() - 1).toArray(new LatLng[0]);
        log.info(latLngs.toString());

        DirectionsRoute route = null;
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
                route = result.routes[0];
                System.out.println("Total distance: " + route.legs[0].distance);
                System.out.println("Total duration: " + route.legs[0].duration);
                System.out.println("Route polyline: " + route.overviewPolyline);
            }
        } catch (ApiException | InterruptedException | IOException e) {
            // Handle exceptions
            e.printStackTrace();
        }
        RouteResponse response = new RouteResponse();
        if (route != null) {
            response.setPolyline(route.overviewPolyline.getEncodedPath());

            List<String> instructions = new ArrayList<>();
            for (DirectionsLeg leg : route.legs) {
                for (DirectionsStep step : leg.steps) {
                    instructions.add(step.htmlInstructions);
                }
            }
            response.setInstructions(instructions);
        }

        return response;

    }
}
