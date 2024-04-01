package com.northernneckgarbage.google_routing;

public class RouteList{
	private Location startLocation;
	private String startLocationName;
	private Location endLocation;
	private String endLocationName;
	private String projectedStartTime;
	private String projectedArrivalTime;
	private String projectedDepartureTime;

	public Location getStartLocation(){
		return startLocation;
	}
	
	public void setStartLocation(Location startLocation){
		this.startLocation = startLocation;
	}

	public String getStartLocationName(){
		return startLocationName;
	}

	public void setStartLocationName(String startLocationName){
		this.startLocationName = startLocationName;
	}

	public Location getEndLocation(){
		return endLocation;
	}

	public void setEndLocation(Location endLocation){
		this.endLocation = endLocation;
	}

	public String getEndLocationName(){
		return endLocationName;
	}
	
	public void setEndLocationName(String endLocationName){
		this.endLocationName = endLocationName;
	}

	public String getProjectedStartTime(){
		return projectedStartTime;
	}

	public void setProjectedStartTime(String projectedStartTime){
		this.projectedStartTime = projectedStartTime;
	}

	public String getProjectedArrivalTime(){
		return projectedArrivalTime;
	}

	public void setProjectedArrivaltime(String projectedArrivalTime){
		this.projectedArrivalTime = projectedArrivalTime;
	}

	public String getProjectedDepartureTime(){
		return projectedDepartureTime;
	}

	public void setProjectedDepartureTime(String projectedDepartureTime){
		this.projectedDepartureTime = projectedDepartureTime;
	}

}
