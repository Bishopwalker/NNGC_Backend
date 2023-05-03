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
 class Geometry {
    private Bounds bounds;
    private Location location;
    private String locationType;
    private Viewport viewport;

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
