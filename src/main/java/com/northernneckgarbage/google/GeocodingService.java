package com.northernneckgarbage.google;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.northernneckgarbage.entity.Customer;
import com.northernneckgarbage.repository.CustomerRepository;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeocodingService {

    Dotenv dotenv = Dotenv.load();
private final CustomerRepository customerRepository;
    private final String apiKey = dotenv.get("GOOGLE_MAPS_API_KEY");
    public GeoApiContext getContext() {
        return new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
    }
    private GeocodingData fetchGeocodingData(String address) throws InterruptedException, ApiException, IOException {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
        com.google.maps.model.GeocodingResult[] results = GeocodingApi.geocode(context, address).await();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Check if results array is empty
        if (results.length == 0) {
            // Optionally, log an error or throw an exception
           log.info("No geocoding results found for address: " + address);
           //#looking for a checked exception good for end of an array

            return null;  // or throw new SomeException("No geocoding results found for address: " + address);
        }

        log.info(gson.toJson(results[0].addressComponents));

        // Deserialize the JSON response into a GeocodingData object
        return gson.fromJson(gson.toJson(results[0]), GeocodingData.class);
    }

    public GeocodingData getGeocoding(String address) throws InterruptedException, ApiException, IOException {
        return fetchGeocodingData(address);
    }

    public GeocodingData getGeocodeByID(long id) throws InterruptedException, ApiException, IOException {
        var user = customerRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        String address = user.getHouseNumber() + " " + user.getStreetName().toUpperCase() + ", " + user.getCity().toUpperCase() + ", " + user.getState().toUpperCase() + " " + user.getZipCode();
        GeocodingData geoData = fetchGeocodingData(address);

        Double userLatitude = user.getLatitude();
        Double userLongitude = user.getLongitude();
        Double geoDataLatitude = geoData.getGeometry().getLocation().getLat();
        Double geoDataLongitude = geoData.getGeometry().getLocation().getLng();

        // Check for null latitude and longitude
        if (userLatitude == null || userLongitude == null ||
                !userLatitude.equals(geoDataLatitude) || !userLongitude.equals(geoDataLongitude)) {
            user.setLatitude(geoDataLatitude);
            user.setLongitude(geoDataLongitude);
            customerRepository.save(user);
        }

        return geoData;
    }

    //GeoCode the entire Database and update the lat and long
    public void updateAllUsersGeocodes() throws InterruptedException, ApiException, IOException {
        List<Customer> users = customerRepository.findAll();
     //   List<Customer> updatedUsers = new ArrayList<>();

        for (Customer user : users) {
            String address = user.getHouseNumber() + " " + user.getStreetName().toUpperCase() + ", " + user.getCity().toUpperCase() + ", " + user.getState().toUpperCase() + " " + user.getZipCode();
            GeocodingData geoData = getGeocoding(address);

            if ((user.getLatitude() == null || user.getLongitude() == null) ||
                    (user.getLatitude() != geoData.getGeometry().getLocation().getLat() ||
                            user.getLongitude() != geoData.getGeometry().getLocation().getLng())) {
                user.setLatitude(geoData.getGeometry().getLocation().getLat());
                user.setLongitude(geoData.getGeometry().getLocation().getLng());
                customerRepository.save(user);
               // updatedUsers.add(user);
            }
        }
    }
}
