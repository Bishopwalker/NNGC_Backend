package com.northernneckgarbage.nngc.google;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
class AddressComponent {
    private String longName;
    private String shortName;
    private List<String> types;

}
@Data
  class Location {
    private double lat;
    private double lng;

    public Location(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

}
 @Data
 class Bounds {
    private Location northeast;
    private Location southwest;

}
 @Data
 class Viewport {
    private Location northeast;
    private Location southwest;

}


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
 public class GeocodingData {
    private List<AddressComponent> addressComponents;
    private String formattedAddress;
    private Object postcodeLocalities;
    private Geometry geometry;
    private List<String> types;
    private boolean partialMatch;
    private String placeId;
    private Object plusCode;

}
