package com.northernneckgarbage.nngc.route4me;

import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import com.route4me.sdk.services.geocoding.GeocoderOptions;
import com.route4me.sdk.services.geocoding.GeocoderOptions.GeocoderDetails;
import com.route4me.sdk.services.geocoding.GeocodingManager;
import com.route4me.sdk.services.routing.Address;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.mapping.Array;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteService {
   private final CustomerRepository customerRepository;
    Dotenv dotenv = Dotenv.load();



          //create a function to grab all user from the database
            //Save all the addresses to a list
            //Pass the list to the geocoder
            //Save the results to the database

    public String geoCodeEntireDB(){
        //get all the addresses from the database
        //return the addresses

        GeocodingManager geocodingManager = new GeocodingManager(dotenv.get("ROUTE_4_ME_API_KEY"));
        log.info("Route4Me API Key: " + dotenv.get("ROUTE_4_ME_API_KEY"));
        GeocoderOptions geocoderOptions = new GeocoderOptions();
        geocoderOptions.setDetailed(GeocoderOptions.GeocoderDetails.DETAILED);
        geocoderOptions.setMaxThreads(40);
        geocoderOptions.setMaxRetries(15);
        geocoderOptions.setMaxTimeout(5001);
        log.info("Geocoder max threads: " + geocoderOptions.getMaxThreads());

        var users = customerRepository.findAll();
        //save all addresses to a Array list in the from houseNumber streetName coma city coma state zip

        List<String> addresses = users.stream().map(customer ->
                customer.getHouseNumber()
                        + " " + customer.getStreetName().toUpperCase() + ", "
                        + customer.getCity().toUpperCase() + ", "
                        + customer.getState().toUpperCase() + " "
                        + customer.getZipCode()).collect(Collectors.toList());

        List<Address> geocodedAddresses =  geocodingManager.bulkGeocoder(  addresses, geocoderOptions);
        return geocodedAddresses.stream().map(address -> address.getGeocodings()).collect( Collectors.toList()).toString();

    }

     public String geoCodeFromDatabase(Long id){
         //get the address from the database
         //return the address

         GeocodingManager geocodingManager = new GeocodingManager(dotenv.get("ROUTE_4_ME_API_KEY"));
     log.info("Route4Me API Key: " + dotenv.get("ROUTE_4_ME_API_KEY"));
         GeocoderOptions geocoderOptions = new GeocoderOptions();
         geocoderOptions.setDetailed(GeocoderOptions.GeocoderDetails.DETAILED);
         geocoderOptions.setMaxThreads(40);
         geocoderOptions.setMaxRetries(15);
         geocoderOptions.setMaxTimeout(5001);
         log.info("Geocoder max threads: " + geocoderOptions.getMaxThreads());

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

}
