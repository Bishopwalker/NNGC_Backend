package com.northernneckgarbage.nngc.google_routing;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RouteRequest {
	private final String startTime;
	private final String optimizationType;
	private final Location startLocation;
	private final List<Location> routeLocations;
}