package com.northernneckgarbage.nngc.google_routing;

import java.util.List;

public class RouteResponse{
	private String startingTime;
	private String destinationArrivalTime;
	private int totalStops;
	private String totalDuration;
	private String routeDistance;
	private String routeType;
	private List<RouteList> routeList;

	public String getStartingTime(){
		return startingTime;
	}

	public void setStartingTime(String startingTime){
		this.startingTime = startingTime;
	}

	public String getDestinationArrivalTime(){
		return destinationArrivalTime;
	}

	public void setDestinationArrivalTime(String destinationArrivalTime){
		this.destinationArrivalTime = destinationArrivalTime;
	}

	public int getTotalStops(){
		return totalStops;
	}

	public void setTotalStops(int totalStops){
		this.totalStops = totalStops;
	}

	public String getTotalDuration(){
		return totalDuration;
	}

	public void setTotalDuration(String totalDuration){
		this.totalDuration = totalDuration;
	}

	public String getRouteDistance(){
		return routeDistance;
	}

	public void setRouteDistance(String routeDistance){
		this.routeDistance = routeDistance;
	}

	public String getRouteType(){
		return routeType;
	}

	public void setRouteType(String routeType){
		this.routeType = routeType;
	}

	public List<RouteList> getRouteList(){
		return routeList;
	}

	public void setRouteList(List<RouteList> routeList){
		this.routeList = routeList;
	}

}
