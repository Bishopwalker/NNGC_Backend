package com.northernneckgarbage.nngc.route4me;

import com.route4me.sdk.exception.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("auth/route/")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @GetMapping("/geoCode/{id}")
    public String test(@PathVariable Long id) throws APIException {

        return  routeService.geoCodeSingleUserFromDatabase(id);
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
