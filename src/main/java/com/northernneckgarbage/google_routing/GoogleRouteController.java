package com.northernneckgarbage.google_routing;

import com.northernneckgarbage.dbConfig.RouteResponse;
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
	@GetMapping("/hello")
	public String getHello(){
		return "HELLO FROM GET";
	}
}
