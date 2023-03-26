package com.northernneckgarbage.nngc.route4me;

import com.route4me.sdk.exception.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("auth/route/")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @GetMapping("/geoCode/{id}")
    public String test(@PathVariable Long id) throws APIException {

        return  routeService.geoCodeFromDatabase(id);
    }

    @GetMapping("/geoCodeAll")
    public String test() throws APIException {

        return  routeService.geoCodeEntireDB();
    }

    @GetMapping("/route")
    public String testRoute()  {

        return  routeService.createRoute4OneDriver();
    }


}
