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
@RestController
public class MainController {

    @Autowired
    private RegisterService registerService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private ReservationService reservationService;

    @CrossOrigin
    @PostMapping(path = "/api/register", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest){
        System.out.println("Register request from " + registerRequest.getFirstName() + " " + registerRequest.getLastName());
        return registerService.register(registerRequest);
    }

    @CrossOrigin
    @PostMapping(path = "/api/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> login(@RequestBody LoginRequest loginRequest){
        System.out.println("Login attempt from " + loginRequest.getEmail());
        return loginService.login(loginRequest);
    }

    @CrossOrigin
    @PostMapping(path = "/api/reservation", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> createReservation(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,
            @RequestBody ReservationPostRequest reservationPostRequest
    ){
        System.out.println("Reservation made: " + reservationPostRequest.toString());
        return reservationService.post(authorizationHeader, reservationPostRequest);
    }

    @CrossOrigin
    @GetMapping(path = "/api/reservation", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> getReservation(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,
            @RequestBody ReservationGetRequest reservationGetRequest
    ){
        System.out.println("Reservation retrieved: " + reservationGetRequest.toString());
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