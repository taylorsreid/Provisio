package provisio.api;

import org.springframework.http.*;
import provisio.api.models.requests.LoginRequest;
import provisio.api.models.requests.RegisterRequest;
import provisio.api.models.requests.ReservationGetRequest;
import provisio.api.models.requests.ReservationPostRequest;
import provisio.api.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RequestMapping(path = "/Provisio/api")
@RestController
public class MainController {

    @Autowired
    private RegisterService registerService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private ReservationService reservationService;

    @PostMapping(path = "/register", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest){
        return registerService.register(registerRequest);
    }

    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> login(@RequestBody LoginRequest loginRequest){
        return loginService.login(loginRequest);
    }

    @PostMapping(path = "/reservation", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> createReservation(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,
            @RequestBody ReservationPostRequest reservationPostRequest
    ){
        return reservationService.post(authorizationHeader, reservationPostRequest);
    }

    @GetMapping(path = "/reservation", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> getReservation(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,
            @RequestBody ReservationGetRequest reservationGetRequest
    ){
        return reservationService.get(authorizationHeader, reservationGetRequest);
    }

//    @GetMapping(path = "/shift", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public @ResponseBody ResponseEntity<String> getShifts(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody GetReservation getReservation){
//        if(authorizationService.verifyAuthorizationHeader(authorizationHeader)){
//            return reservationService.getShifts(authorizationService.getUserIdFromAuthorizationHeader(authorizationHeader), getReservation);
//        }
//        else{
//            return new ResponseEntity<>(new GenericResponse(false, "BAD TOKEN").toString(), HttpStatus.UNAUTHORIZED);
//        }
//    }

}