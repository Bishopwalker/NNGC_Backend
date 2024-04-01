package com.northernneckgarbage.google_routing;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class RouteRequest {
	private final String startTime;
	private final String optimizationType;
	private final Location startLocation;
	private final List<Location> routeLocations;
}