package com.northernneckgarbage.nngc.google_routing;

import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.entity.dto.CustomerDTO;

import java.util.List;

@lombok.Data
public class RouteResponse{
	private String startingTime;
	private String destinationArrivalTime;
	private int totalStops;
	private String totalDuration;
	private String routeDistance;
	private String routeType;
	private List<RouteList> routeList;
	private Customer customerInfo;



}
