package provisio.api;

import org.springframework.http.*;
import provisio.api.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class MainController {

    @Autowired
    private RegisterService registerService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private ReservationService reservationService;

    @PostMapping(path = "/api/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> register(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String firstName,
            @RequestParam String lastName
    ){
        return registerService.register(email, password, firstName, lastName);
    }

    @PostMapping(path = "/api/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> login(
            @RequestParam String email,
            @RequestParam String password
    ){
        return loginService.login(email, password);
    }

    @PostMapping(path = "/api/reservation", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> createReservation(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ){
        return reservationService.post(authorizationHeader);
    }

    @GetMapping(path = "/api/reservation", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> getReservation(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
    ){
        return reservationService.get(authorizationHeader);
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