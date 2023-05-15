package com.northernneckgarbage.nngc.google_routing;

import java.util.List;

public class RouteRequest{
	private String startTime;
	private String optimizationType;
	private Location startLocation;
	private List<Location> routeLocations;

	public String getStartTime(){
		return startTime;
	}

	public void setStartTime(String startTime){
		this.startTime = startTime;
	}

	public String getOptimizationType(){
		return optimizationType;
	}

	public void setOptimizationType(String optimizationType){
		this.optimizationType = optimizationType;
	}

	public Location getStartLocation(){
		return startLocation;
	}

	public void setStartLocation(Location startLocation){
		this.startLocation = startLocation;
	}

	public List<Location> getRouteLocations(){
		return routeLocations;
	}

	public void setRouteLocations(List<Location> routeLocations){
		this.routeLocations = routeLocations;
	}
}
