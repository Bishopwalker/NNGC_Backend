package com.northernneckgarbage.nngc.google_routing;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/nngc/google_routing")
public class GoogleRouteController {
	private final RouteService routeService;
	public GoogleRouteController(RouteService routeService){
		this.routeService = routeService;
	}
	@PostMapping("/route")
	public RouteResponse postGreeting(@RequestBody RouteRequest routeRequest){
		// String startTime = request.getStartTime();
		// String optimizationType = request.getOptimizationType();
		// String startLocation = Double.toString(request.getStartLocation().getLatitude());
		// String response = "Start Time: " + startTime + " Optimization Type: " + optimizationType+" Start Location: "+startLocation;
		// RouteResponse routeResponse = new RouteResponse();
		return routeService.calculateOptimizedRoute(routeRequest);
	}
	@GetMapping("/auto-route")
	public ResponseEntity<RouteResponse> getAutoRoute(){
		RouteResponse routeResponse = routeService.calculateOptimizedRoute((RouteRequest) routeService.calculateRoutesForAllCustomers());
		return ResponseEntity.ok(routeResponse);
	}

}
