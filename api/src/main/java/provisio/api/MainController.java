package provisio.api;

import org.springframework.http.*;
import provisio.api.models.requests.*;
import provisio.api.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping(path = "/Provisio/api")
@RestController
public class MainController {

    @Autowired
    private RegisterService registerService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private ReservationService reservationService;

    @CrossOrigin
    @PostMapping(path = "/register", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest){
        return registerService.register(registerRequest);
    }

    @CrossOrigin
    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> login(@RequestBody LoginRequest loginRequest){
        return loginService.login(loginRequest);
    }

    @CrossOrigin
    @PostMapping(path = "/reservations/new", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> createReservation(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestBody ReservationPostRequest reservationPostRequest
    ){
        return reservationService.post(authorizationHeader, reservationPostRequest);
    }

    @CrossOrigin
    @PostMapping(path = "/reservations/getByReservationId", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> getReservationByReservationId(@RequestBody ReservationGetByReservationIdRequest reservationGetByReservationIdRequest){
        return reservationService.getByReservationId(reservationGetByReservationIdRequest);
    }

    @CrossOrigin
    @PostMapping(path = "/reservations/getByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> getReservationByUserId(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorizationHeader){
            return reservationService.getByUserId(authorizationHeader);
    }
}
