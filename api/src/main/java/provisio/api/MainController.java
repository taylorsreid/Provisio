package provisio.api;

import org.springframework.http.HttpHeaders;
import provisio.api.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MainController {

    @Autowired
    private RegisterService registerService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private AuthorizationService authorizationService;
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
    public @ResponseBody ResponseEntity<String> createShifts(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestBody List<ReservationService> shiftRequestList)
    {
        if(authorizationService.verifyAuthorizationHeader(authorizationHeader)){
            return reservationService.createShifts(authorizationService.getUserIdFromAuthorizationHeader(authorizationHeader), shiftRequestList);
        }
        else{
            return new ResponseEntity<>(new GenericResponse(false, "BAD TOKEN").toString(), HttpStatus.UNAUTHORIZED);
        }
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