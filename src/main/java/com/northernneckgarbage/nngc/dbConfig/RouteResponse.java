package com.northernneckgarbage.nngc.dbConfig;

import com.northernneckgarbage.nngc.google_routing.RouteList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteResponse{
	private String startingTime;
	private String destinationArrivalTime;
	private int totalStops;
	private String totalDuration;
	private String routeDistance;
	private String routeType;
	private List<RouteList> routeList;
	private  String polyline;
	private  List<String> instructions;



}
