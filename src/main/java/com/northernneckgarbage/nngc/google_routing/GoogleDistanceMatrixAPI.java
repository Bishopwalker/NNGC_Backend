package com.northernneckgarbage.nngc.google_routing;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.lang.Integer;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

@Slf4j
@Service
@RequiredArgsConstructor
@Data
public class GoogleDistanceMatrixAPI {

    private RestTemplate restTemplate;

    Dotenv dotenv = Dotenv.load();
  final String apiKey = dotenv.get("GOOGLE_MAPS_API_KEY");




    public int getDistance(double originLat, double originLng, double destLat, double destLng) {
        String origin = originLat + "," + originLng;
        String destination = destLat + "," + destLng;

        String apiUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins={origin}&destinations={destination}&key={apiKey}";

        ResponseEntity<DistanceMatrixResponse> responseEntity = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                null,
                DistanceMatrixResponse.class,
                origin,
                destination,
                apiKey
        );

        DistanceMatrixResponse response = responseEntity.getBody();

        if (response != null && response.getRows().length > 0 && response.getRows()[0].getElements().length > 0) {
            DistanceElement distanceElement = response.getRows()[0].getElements()[0];
            if (distanceElement.getStatus().equals("OK")) {
                return distanceElement.getDistance().getValue();
            }
        }

        throw new RuntimeException("Failed to retrieve distance from Google Distance Matrix API");
    }

    public int getDuration(double originLat, double originLng, double destLat, double destLng) {
        String origin = originLat + "," + originLng;
        String destination = destLat + "," + destLng;

        String apiUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins={origin}&destinations={destination}&key={apiKey}";

        ResponseEntity<DistanceMatrixResponse> responseEntity = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                null,
                DistanceMatrixResponse.class,
                origin,
                destination,
                apiKey
        );

        DistanceMatrixResponse response = responseEntity.getBody();

        if (response != null && response.getRows().length > 0 && response.getRows()[0].getElements().length > 0) {
            DistanceElement distanceElement = response.getRows()[0].getElements()[0];
            if (distanceElement.getStatus().equals("OK")) {
                return distanceElement.getDuration().getValue();
            }
        }

        throw new RuntimeException("Failed to retrieve duration from Google Distance Matrix API");
    }
	

    public String getMetric(double originLat, double originLng, double destLat, double destLng) {
        String origin = originLat + "," + originLng;
        String destination = destLat + "," + destLng;

        String apiUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins={origin}&destinations={destination}&key={apiKey}";

        ResponseEntity<DistanceMatrixResponse> responseEntity = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                null,
                DistanceMatrixResponse.class,
                origin,
                destination,
                apiKey
        );

        DistanceMatrixResponse response = responseEntity.getBody();

        if (response != null && response.getRows().length > 0 && response.getRows()[0].getElements().length > 0) {
            DistanceElement distanceElement = response.getRows()[0].getElements()[0];
            if (distanceElement.getStatus().equals("OK")) {
				String durationMetric = Integer.toString(distanceElement.getDuration().getValue());
				String distanceMetric = Integer.toString(distanceElement.getDistance().getValue());
                return durationMetric + ":" + distanceMetric;
            }
        }

        throw new RuntimeException("Failed to retrieve duration from Google Distance Matrix API");
    }
	
    public String getPlusCodeName(String lat, String lng) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+lat+","+lng+"&key="+apiKey;

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

		// parse response string using Gson
        Gson gson = new Gson();
        JsonElement element = gson.fromJson(response.getBody(), JsonElement.class);

        return element.getAsJsonObject().get("plus_code").getAsJsonObject().get("compound_code").getAsString();
    }

    private static class DistanceMatrixResponse {
        private DistanceRow[] rows;

        public DistanceRow[] getRows() {
            return rows;
        }

        public void setRows(DistanceRow[] rows) {
            this.rows = rows;
        }
    }

    private static class DistanceRow {
        private DistanceElement[] elements;

        public DistanceElement[] getElements() {
            return elements;
        }

        public void setElements(DistanceElement[] elements) {
            this.elements = elements;
        }
    }

    private static class DistanceElement {
        private Distance distance;
        private Duration duration ;
        private String status;

        public Distance getDistance() {
            return distance;
        }

        public void setDistance(Distance distance) {
            this.distance = distance;
        }

        public Duration getDuration() {
            return duration;
        }

        public void setDuration(Duration duration) {
            this.duration = duration;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    private static class Distance {
        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    private static class Duration {
        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}

