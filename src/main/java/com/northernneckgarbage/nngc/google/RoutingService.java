package com.northernneckgarbage.nngc.google;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import com.northernneckgarbage.nngc.dbConfig.RouteResponse;
import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.entity.dto.CustomerRouteDetailsDTO;
import com.northernneckgarbage.nngc.entity.dto.CustomerRouteInfoDTO;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoutingService {
    private final CustomerRepository customerRepository;
    private final GeocodingService geocodingService;

public static int totalUsers;
public static int totalEnabledUsers;

    public RouteResponse createRoute4OneDriver(int pageNumber, Optional<String> county) throws IOException, InterruptedException, ApiException {
        GeoApiContext context = geocodingService.getContext();
//     search for enabled users in the database and create a list of those users first
       Page<Customer> enabledUsers = null;
        // Step 2: Fetch all customer addresses from the customer repository
      //  var users = customerRepository.findAll(PageRequest.of(pageNumber - 1, 25));
        Page<Customer>allUsers=null;
if(county.isPresent()){
    enabledUsers = customerRepository.findEnabledCustomersByCounty(PageRequest.of(pageNumber - 1, 25), county.get());
    totalUsers = (int) customerRepository.countByCounty(county.get());  // Update totalUsers with the total count of users
    log.info("totalUsers: " + totalUsers);
    totalEnabledUsers = (int) enabledUsers.getTotalElements();  // Update totalEnabledUsers with the total count of enabled users
    // Step 3: Create a list of LatLng objects from the customer addresses
    log.info("enabledUsersCount: " + totalEnabledUsers);
}else{
    allUsers = customerRepository.findAll(PageRequest.of(pageNumber - 1, 25));
    totalUsers = (int) customerRepository.count();
    totalEnabledUsers = (int) customerRepository.count();
}
    // Step 3: Create a list of CustomerRouteDetailsDTO objects from the customer addresses
    List<CustomerRouteDetailsDTO> customerRouteDetails = allUsers == null? enabledUsers.stream().map(user -> {
                CustomerRouteInfoDTO customerInfo = user.toCustomerRouteInfoDTO();
                LatLng location = new LatLng(customerInfo.getLatitude(), customerInfo.getLongitude());
                return CustomerRouteDetailsDTO.builder()
                        .customerInfo(customerInfo)
                        .location(location)
                        .build();
            })
            .toList() : allUsers.stream().map(user -> {
                CustomerRouteInfoDTO customerInfo = user.toCustomerRouteInfoDTO();
                LatLng location = new LatLng(customerInfo.getLatitude(), customerInfo.getLongitude());
                return CustomerRouteDetailsDTO.builder()
                        .customerInfo(customerInfo)
                        .location(location)
                        .build();
            }).toList();

// Now extract LatLng objects from the CustomerRouteDetailsDTO list for further processing
        List<LatLng> latLngs = customerRouteDetails.stream()
                .map(CustomerRouteDetailsDTO::getLocation)
                .toList();

// Now extract LatLng objects from the CustomerRouteInfo list


// Steps 4-5: Set the first address as the origin, the last address as the destination, and the remaining addresses as waypoints
        LatLng origin;
        LatLng destination;
        LatLng[] waypoints;
        if(latLngs.size() >1){
            origin = latLngs.get(0);
            destination = latLngs.get(latLngs.size() - 1);
            waypoints = latLngs.subList(1, latLngs.size() - 1).toArray(new LatLng[0]);
        } else if (latLngs.size() == 1){
            origin = latLngs.get(0);
            destination = latLngs.get(0);
            waypoints = new LatLng[0];
        }else{
            throw new RuntimeException("No customers found in the county");
        }
       // = latLngs.subList(1, latLngs.size() - 1).toArray(new LatLng[0]);
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
                    .departureTimeNow()
                    .origin("37.968418,-76.4839841")
                              .await();

            // Step 8-9: Handle the response and print the route information
            if (result != null && result.routes != null && result.routes.length > 0) {
                route = result.routes[0];
                log.info("Total distance: " + route.legs[0].distance);
                log.info("Total duration: " + route.legs[0].duration);
                log.info("Route polyline: " + route.overviewPolyline);
            }
        } catch (ApiException | InterruptedException | IOException e) {
            // Handle exceptions
            log.info("Error creating route for driver" + e.getMessage());
        }

        return getRouteResponse(route, customerRouteDetails);

    }

    @NotNull
    private static RouteResponse getRouteResponse(DirectionsRoute route, List<CustomerRouteDetailsDTO> customerRouteDetails) {
        if (route != null) {
            int totalDistance = 0;
            int totalDuration = 0;
            int totalStops = route.legs.length - 1;
            List<String> instructions = new ArrayList<>();

            for (DirectionsLeg leg : route.legs) {
                for (DirectionsStep step : leg.steps) {
                    instructions.add(step.htmlInstructions);
                    totalDistance += (int) leg.distance.inMeters;
                    totalDuration += (int) leg.duration.inSeconds;
                }
            }

            return RouteResponse.builder()
                    .polyline(route.overviewPolyline.getEncodedPath())
                    .routeDistance(String.valueOf(totalDistance/1609.344 ))
                    .totalDuration(String.valueOf(totalDuration/60))
                    .totalStops(totalStops)
                    .instructions(instructions)
                    .customerRouteDetails(customerRouteDetails)
                    .totalCustomers(totalUsers)
                    .build();
        }

        // Return an empty RouteResponse object or null, as per your use case, if route is null
        return RouteResponse.builder().build();
    }
}

