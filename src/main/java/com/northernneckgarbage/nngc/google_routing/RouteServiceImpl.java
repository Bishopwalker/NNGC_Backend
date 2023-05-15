package com.northernneckgarbage.nngc.google_routing;

import java.util.ArrayList;
import java.util.List;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RouteServiceImpl implements RouteService{
	@Override
	public RouteResponse calculateOptimizedRoute(RouteRequest routeRequest){
		RouteResponse routeResponse = new RouteResponse();

		RestTemplate restTemplate = new RestTemplate();


		GoogleDistanceMatrixAPI googleDistanceMatrixAPI = new GoogleDistanceMatrixAPI();
		googleDistanceMatrixAPI.setRestTemplate(restTemplate);


		Location startLocation = routeRequest.getStartLocation();
		String startTime = routeRequest.getStartTime();
		String routeType = routeRequest.getOptimizationType();

		double startLat = startLocation.getLatitude();
		double startLng = startLocation.getLongitude();

		double originLat = startLat; // Latitude of origin (New York)
		double originLng = startLng; // Longitude of origin (New York)

		// System.out.println(originLat);
		// System.out.println(originLng);

		List<Location> routeLocations = routeRequest.getRouteLocations();

		List<Location> locations = new ArrayList<Location>();
		locations.add(startLocation);

		for (Location routeLocation : routeLocations) {
			locations.add(routeLocation);
		}

		int[][] distances = new int[locations.size()][locations.size()];
		int[][] durations = new int[locations.size()][locations.size()];
		String[][] metrics = new String[locations.size()][locations.size()];
		for (int i = 0; i < locations.size(); i++) {
			for (int j = 0; j < locations.size(); j++) {
				if (i != j) {
					Location originLocation = locations.get(i);
					Location destLocation = locations.get(j);

					Double orgLat = originLocation.getLatitude();
					Double orgLng = originLocation.getLongitude();
					Double dstLat = destLocation.getLatitude();
					Double dstLng = destLocation.getLongitude();

					String metric = googleDistanceMatrixAPI.getMetric(orgLat, orgLng, dstLat, dstLng);
					String[] mtrParts = metric.split(":");

					// distances[i][j] = googleDistanceMatrixAPI.getDistance(originLocation.getLatitude(), originLocation.getLongitude(), destLocation.getLatitude(), destLocation.getLongitude());
					durations[i][j] = Integer.parseInt(mtrParts[0]);
					distances[i][j] = Integer.parseInt(mtrParts[1]);
				} else {
					distances[i][j] = 0;
				}
			}
		}

		int totalLocations = distances[0].length;
		//System.out.println("durations");
		// Printing the 2D array durations
		for (int i = 0; i < durations.length; i++) {
			for (int j = 0; j < durations[i].length; j++) {
				//System.out.print(durations[i][j] + " ");
			}
			//System.out.println(); // Move to the next line after printing each row
		}
		//System.out.println("distances");
		// Printing the 2D array distances
		for (int i = 0; i < distances.length; i++) {
			for (int j = 0; j < distances[i].length; j++) {
				//System.out.print(distances[i][j] + " ");
			}
			//System.out.println(); // Move to the next line after printing each row
		}
		
		double destLat = 34.0522; // Latitude of destination (Los Angeles)
		double destLng = -118.2437; // Longitude of destination (Los Angeles)

		int distance = googleDistanceMatrixAPI.getDistance(originLat, originLng, destLat, destLng);
		String distanceString = Integer.toString(distance);
		// System.out.println("Distance between New York and Los Angeles: " + distance + " meters");
		PathInfo pathInfo = new PathInfo(distances, totalLocations);

		// int noOfVertices = pathInfo.getNoOfVertices();
		// System.out.println("No of vertices: " + noOfVertices);

		pathInfo.calculatePath();
		ArrayList<Integer> optimalPath = pathInfo.getOptimalPath();

		int minimalCost = pathInfo.getTotalMinimalCost();
		int totalStops = routeLocations.size()+1;

		
		// String plusCode = googleDistanceMatrixAPI.getPlusCodeName();
		String metric = googleDistanceMatrixAPI.getMetric(originLat, originLng, destLat, destLng);

		//System.out.println(optimalPath);
		//System.out.println(minimalCost);
		// System.out.println(plusCode);
		// System.out.println(metric);

        int totalDuration = 0;
		List<RouteList> routeList = new ArrayList<RouteList>();
		int journeyTimeCounter = Integer.parseInt(startTime);

        for (int i = 0; i < optimalPath.size()-1; i++) {
            int row = optimalPath.get(i)-1;
            int col = optimalPath.get(i+1)-1;
			int duration = durations[row][col];
            totalDuration += duration;
			RouteList route = new RouteList();
			Location startLoc = locations.get(row);
			Location endLoc = locations.get(col);

			String startLocName = googleDistanceMatrixAPI.getPlusCodeName(String.valueOf(startLoc.getLatitude()), String.valueOf(startLoc.getLongitude()));
			String endLocName = googleDistanceMatrixAPI.getPlusCodeName(String.valueOf(endLoc.getLatitude()), String.valueOf(endLoc.getLongitude()));

			String projStartTime = Integer.toString(journeyTimeCounter);
			journeyTimeCounter += duration;
			String projArrivTime = Integer.toString(journeyTimeCounter);
			journeyTimeCounter += 300;
			String projDprtTime = Integer.toString(journeyTimeCounter);

			route.setStartLocation(startLoc);
			route.setStartLocationName(startLocName);
			route.setEndLocation(endLoc);
			route.setEndLocationName(endLocName);
			route.setProjectedStartTime(projStartTime);
			route.setProjectedArrivaltime(projArrivTime);
			route.setProjectedDepartureTime(projDprtTime);
			
			routeList.add(route);
        }

		totalDuration += (optimalPath.size()-1)*300;		

		String destinationArrivalTime = Integer.toString(Integer.parseInt(startTime) + totalDuration);

		routeResponse.setStartingTime(startTime);
		routeResponse.setDestinationArrivalTime(destinationArrivalTime);
		routeResponse.setTotalStops(totalStops);
		routeResponse.setTotalDuration(Integer.toString(totalDuration));
		routeResponse.setRouteDistance(Integer.toString(minimalCost));
		routeResponse.setRouteType(routeType);
		routeResponse.setRouteList(routeList);

		pathInfo.reset();

		return routeResponse;
	}
}

interface RouteService{
	RouteResponse calculateOptimizedRoute(RouteRequest request);
}
