package com.northernneckgarbage.nngc.route4me;

import com.northernneckgarbage.nngc.repository.CustomerRepository;
import com.route4me.sdk.exception.APIException;
import com.route4me.sdk.services.geocoding.GeocoderOptions;
import com.route4me.sdk.services.geocoding.GeocodingManager;
import com.route4me.sdk.services.routing.*;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteService {
   private final CustomerRepository customerRepository;
    Dotenv dotenv = Dotenv.load();

    private GeocodingManager geocodingManager;
    private GeocoderOptions geocoderOptions;
    private RoutingManager routingManager;
    public void init() {
        geocodingManager = new GeocodingManager(dotenv.get("ROUTE_4_ME_API_KEY"));
        routingManager = new RoutingManager(dotenv.get("ROUTE_4_ME_API_KEY"));
        log.info("Route4Me API Key: " + dotenv.get("ROUTE_4_ME_API_KEY"));

        geocoderOptions = new GeocoderOptions();
        geocoderOptions.setDetailed(GeocoderOptions.GeocoderDetails.DETAILED);
        geocoderOptions.setMaxThreads(40);
        geocoderOptions.setMaxRetries(15);
        geocoderOptions.setMaxTimeout(5001);

        log.info("Geocoder max threads: " + geocoderOptions.getMaxThreads());
    }

          //create a function to grab all user from the database
            //Save all the addresses to a list
            //Pass the list to the geocoder
            //Save the results to the database

    public String geoCodeEntireDB(){
        //get all the addresses from the database
        //return the addresses

        init();

        var users = customerRepository.findAll();
        //save all addresses to a Array list in the from houseNumber streetName coma city coma state zip

        List<String> addresses = users.stream().map(customer ->
                customer.getHouseNumber()
                        + " " + customer.getStreetName().toUpperCase() + ", "
                        + customer.getCity().toUpperCase() + ", "
                        + customer.getState().toUpperCase() + " "
                        + customer.getZipCode())
                .collect(Collectors.toList());

        List<Address> geocodedAddresses =  geocodingManager.bulkGeocoder(  addresses, geocoderOptions);

        //loop through the list of addresses and save the results to the database
        for (int i = 0; i < geocodedAddresses.size(); i++) {
            users.get(i).setGeoLocation(geocodedAddresses.get(i).getGeocodings().get(0).toString());
            users.get(i).setLatitude(geocodedAddresses.get(i).getGeocodings().get(0).getLatitude());
            users.get(i).setLongitude(geocodedAddresses.get(i).getGeocodings().get(0).getLongitude());
            customerRepository.save(users.get(i));
        }



        return geocodedAddresses.stream().map(address -> address.getGeocodings()).collect( Collectors.toList()).toString();

    }

     public String geoCodeFromDatabase(Long id){
         //get the address from the database
         //return the address
init();

          var user = customerRepository.findById(id);
         //save all addresses to a Array list in the from houseNumber streetName coma city coma state zip

         List<String> addresses = user.stream().map(customer ->
                 customer.getHouseNumber()
                         + " " + customer.getStreetName().toUpperCase() + ", "
                         + customer.getCity().toUpperCase() + ", "
                         + customer.getState().toUpperCase() + " "
                         + customer.getZipCode()).collect(Collectors.toList());


            log.debug("Running Geocoder with " + geocoderOptions.getMaxThreads() + " threads");

         long startTime = System.currentTimeMillis();

            List<Address> geocodedAddresses =  geocodingManager.bulkGeocoder(  addresses, geocoderOptions);
log.info("Geocoded addresses: " + geocodedAddresses);

 user.get().setGeoLocation(geocodedAddresses.stream().map(Address::getGeocodings).collect( Collectors.toList()).toString());
         customerRepository.save(user.get());
            return geocodedAddresses.stream().map(address -> address.getGeocodings()).collect( Collectors.toList()).toString();


     }

     //create a route from all the addresses in the database
public String createRoute4OneDriver() {
    init();
    OptimizationParameters optParameters = new OptimizationParameters();

    Parameters parameters = new Parameters();
    var users = customerRepository.findAll();
    List<Address> addresses = users.stream().map(customer ->
                    new Address(customer.getHouseNumber()
                            + " " + customer.getStreetName().toUpperCase() + ", "
                            + customer.getCity().toUpperCase() + ", "
                            + customer.getState().toUpperCase() + " "
                            + customer.getZipCode(), customer.getLatitude(), customer.getLongitude()))
            .collect(Collectors.toList());
    parameters.setAlgorithmType(Constants.AlgorithmType.TSP.getValue());
    parameters.setStoreRoute(Boolean.FALSE);
    parameters.setShareRoute(Boolean.FALSE);
    parameters.setRouteName("Single Driver Route 10 Stops");
    parameters.setOptimize(Constants.Optimize.DISTANCE.toString());
    parameters.setDistanceUnit(Constants.DistanceUnit.MI.toString());
    parameters.setDeviceType(Constants.DeviceType.WEB.toString());
    optParameters.setParameters(parameters);
    optParameters.setAddresses(addresses);
    DataObject responseObject = null;
    try {
        responseObject = routingManager.runOptimization(optParameters);
        System.out.println("Optimization Problem ID:" + responseObject.getOptimizationProblemId());
        System.out.println("State:" + Constants.OptimizationState.get(responseObject.getState().intValue()));
        if (responseObject.getAddresses() != null) {
            for (Address address : responseObject.getAddresses()) {
                System.out.println(address);
            }
        }
    } catch (APIException e) {
        //handle exception
        e.printStackTrace();
    }

    return responseObject.getAddresses().toString();
}

}
