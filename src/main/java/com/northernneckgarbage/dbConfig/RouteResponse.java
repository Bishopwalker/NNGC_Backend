package com.northernneckgarbage.dbConfig;

import com.northernneckgarbage.entity.dto.CustomerRouteDetailsDTO;
import com.northernneckgarbage.google.InstructionWithCustomerId;
import com.northernneckgarbage.google_routing.RouteList;
import lombok.*;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)  //
 	public class RouteResponse {
		public String polyline;
		public String routeDistance;
		public String totalDuration;
		public Integer totalStops;
		public List<InstructionWithCustomerId> instructions;
		public List<CustomerRouteDetailsDTO> customerRouteDetails;
		public String startingTime;
		public String routeType;
		public List<RouteList> routeList;
		public String destinationArrivalTime;
		public int totalUsers;
		public int totalEnabledUsers;
	}





