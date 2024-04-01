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
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoutingService {
    private final CustomerRepository customerRepository;
    private final GeocodingService geocodingService;

    public static int totalUsers;
    public static int totalEnabledUsers;

//    public RouteResponse createRoute4OneDriver(int pageNumber, Optional<String> county) {
//        GeoApiContext context = geocodingService.getContext();
//        // Fetch the NNGC_ADMIN
//        CustomerRouteInfoDTO nngcAdminInfo = fetchNNGCAdminInfo();
//        // Create a LatLng object for the NNGC_ADMIN's location
//        LatLng nngcAdminLocation = new LatLng(nngcAdminInfo.getLatitude(), nngcAdminInfo.getLongitude());
//        // Fetch the customers in the county
//        Page<Customer> customers = fetchCustomers(pageNumber, county);
//        // Create a list of CustomerRouteDetailsDTO objects
//        List<CustomerRouteDetailsDTO> customerRouteDetails = createCustomerRouteDetails(customers);
//        // Sort the customers based on their distance from the NNGC_ADMIN
//        customerRouteDetails.sort(Comparator.comparing(dto -> distanceBetween(nngcAdminLocation, dto.getLocation())));
//        List<LatLng> latLngs = customerRouteDetails.stream()
//                .map(CustomerRouteDetailsDTO::getLocation)
//                .collect(Collectors.toList());
//        // Convert the list of LatLng objects to an array of LatLng objects
//        LatLng[] waypoints = latLngs.toArray(new LatLng[0]);
//        // Check if customers exist in the county
//        checkCustomersExist(latLngs);
//        // Create a DirectionsRoute object
//        DirectionsRoute route = createDirectionsRoute(context, nngcAdminLocation, waypoints);
//        // Return a RouteResponse object
//        return getRouteResponse(route, customerRouteDetails);
//    }
public RouteResponse createRoute4OneDriver(int pageNumber, Optional<String> county) {
    GeoApiContext context = geocodingService.getContext();
    // Fetch the NNGC_ADMIN
    CustomerRouteInfoDTO nngcAdminInfo = fetchNNGCAdminInfo();
    // Create a LatLng object for the NNGC_ADMIN's location
    LatLng nngcAdminLocation = new LatLng(nngcAdminInfo.getLatitude(), nngcAdminInfo.getLongitude());
    // Fetch the customers in the county
    Page<Customer> customers = fetchCustomers(pageNumber, county);
    // Create a list of CustomerRouteDetailsDTO objects
    List<CustomerRouteDetailsDTO> customerRouteDetails = createCustomerRouteDetails(customers);
    // Sort the customers based on their distance from the NNGC_ADMIN
    customerRouteDetails.sort(Comparator.comparing(dto -> distanceBetween(nngcAdminLocation, dto.getLocation())));
    // Check if customers exist in the county
  List<LatLng> latLngs = customerRouteDetails.stream()
    .map(CustomerRouteDetailsDTO::getLocation)
    .collect(Collectors.toList());
checkCustomersExist(latLngs);
    // Return a RouteResponse object
    return getRouteResponse(context, customerRouteDetails);
}
    private CustomerRouteInfoDTO fetchNNGCAdminInfo() {
        return customerRepository.findById(273L)
                .orElseThrow(() -> new RuntimeException("NNGC_ADMIN not found"))
                .toCustomerRouteInfoDTO();
    }

    private Page<Customer> fetchCustomers(int pageNumber, Optional<String> county) {
  var result = county.map(c -> customerRepository.findEnabledCustomersByCounty(PageRequest.of(pageNumber - 1, 25), c))
                .orElseGet(() -> customerRepository.findAll(PageRequest.of(pageNumber - 1, 25)));
  totalUsers = (int) customerRepository.count();
       totalEnabledUsers = (int) result.getTotalElements();
        return result;
    }

    private List<CustomerRouteDetailsDTO> createCustomerRouteDetails(Page<Customer> customers) {
        return customers.stream()
                .map(Customer::toCustomerRouteInfoDTO)
                .map(info -> CustomerRouteDetailsDTO.builder()
                        .customerInfo(info)
                        .location(new LatLng(info.getLatitude(), info.getLongitude()))
                        .build())
                .collect(Collectors.toList());
    }

    private void checkCustomersExist(List<LatLng> latLngs) {
        if (latLngs.isEmpty()) {
            throw new RuntimeException("No customers found in the county");
        }
    }

    private DirectionsRoute createDirectionsRoute(GeoApiContext context, LatLng nngcAdminLocation, LatLng[] waypoints) {
        try {
            DirectionsResult result = DirectionsApi.newRequest(context)
                    .origin(nngcAdminLocation)
                    .destination(nngcAdminLocation)
                    .waypoints(waypoints)
                    .optimizeWaypoints(true)
                    .mode(TravelMode.DRIVING)
                    .departureTimeNow()
                    .await();
            return Optional.ofNullable(result)
                    .map(r -> r.routes[0])
                    .orElse(null);
        } catch (ApiException | InterruptedException | IOException e) {
            log.info("Error creating route for driver" + e.getMessage());
            return null;
        }
    }

//    @NotNull
//    private static RouteResponse getRouteResponse(DirectionsRoute route, List<CustomerRouteDetailsDTO> customerRouteDetails) {
//        if (route != null) {
//            int totalDistance = 0;
//            int totalDuration = 0;
//            int totalStops = route.legs.length - 1;
//            List<InstructionWithCustomerId> instructions = new ArrayList<>();
//
//            //make sure I start with the first customer
//             int currentCustomerIndex = 0;
//            for (DirectionsLeg leg : route.legs) {
//                for (DirectionsStep step : leg.steps) {
//                    InstructionWithCustomerId instructionWithCustomerId = new InstructionWithCustomerId();
//                    instructionWithCustomerId.setInstruction(step.htmlInstructions);
//
//                   if(currentCustomerIndex < customerRouteDetails.size()) {
//                        instructionWithCustomerId.setCustomerInfo(customerRouteDetails.get(currentCustomerIndex).getCustomerInfo());
//
//                    }
//
//                    instructions.add(instructionWithCustomerId);
//                    totalDistance += (int) leg.distance.inMeters;
//                    totalDuration += (int) leg.duration.inSeconds;
//                }
//                currentCustomerIndex++;
//            }
//            log.info(instructions.toString());
//            return RouteResponse.builder()
//                    .polyline(route.overviewPolyline.getEncodedPath())
//                    .routeDistance(String.valueOf(totalDistance/1609.344 ))
//                    .totalDuration(String.valueOf(totalDuration/60))
//                    .totalStops(totalStops)
//                    .instructions(instructions)
//                    .customerRouteDetails(customerRouteDetails)
//                    .totalUsers(totalUsers)
//                    .totalEnabledUsers(totalEnabledUsers)
//                    .build();
//        }
//
//        // Return an empty RouteResponse object or null, as per your use case, if route is null
//        return RouteResponse.builder().build();
//    }

 private RouteResponse getRouteResponse(GeoApiContext context, List<CustomerRouteDetailsDTO> customerRouteDetails) {
    int totalDistance = 0;
    int totalDuration = 0;
    List<InstructionWithCustomerId> instructions = new ArrayList<>();
    LatLng previousLocation = new LatLng(fetchNNGCAdminInfo().getLatitude(), fetchNNGCAdminInfo().getLongitude());
    StringBuilder totalPolyline = new StringBuilder();

    for (CustomerRouteDetailsDTO customer : customerRouteDetails) {
        DirectionsRoute route;
        try {
            route = DirectionsApi.newRequest(context)
                    .origin(previousLocation)
                    .destination(customer.getLocation())
                    .mode(TravelMode.DRIVING)
                    .departureTimeNow()
                    .await().routes[0];
        } catch (ApiException | InterruptedException | IOException e) {
            log.info("Error creating route for driver" + e.getMessage());
            continue;
        }

        for (DirectionsLeg leg : route.legs) {
            for (DirectionsStep step : leg.steps) {
                InstructionWithCustomerId instructionWithCustomerId = new InstructionWithCustomerId();
                instructionWithCustomerId.setInstruction(step.htmlInstructions);
                instructionWithCustomerId.setCustomerInfo(customer.getCustomerInfo());

                instructions.add(instructionWithCustomerId);
                totalDistance += (int) leg.distance.inMeters;
                totalDuration += (int) leg.duration.inSeconds;
            }
        }

        totalPolyline.append(route.overviewPolyline.getEncodedPath());
        previousLocation = customer.getLocation();
    }

    return RouteResponse.builder()
            .polyline(totalPolyline.toString())
            .instructions(instructions)
            .routeDistance(String.valueOf(totalDistance / 1609.344))
            .totalDuration(String.valueOf(totalDuration / 60))
            .totalStops(customerRouteDetails.size() - 1)
            .customerRouteDetails(customerRouteDetails)
            .totalUsers(totalUsers)
            .totalEnabledUsers(totalEnabledUsers)
            .build();
}

    private double distanceBetween(LatLng point1, LatLng point2) {
        double lat1 = point1.lat;
        double lon1 = point1.lng;
        double lat2 = point2.lat;
        double lon2 = point2.lng;

        // Calculate the distance between the two points based on the Haversine formula
        double earthRadius = 6371; // Radius of the earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c; // Distance in km

        return distance;
    }
}