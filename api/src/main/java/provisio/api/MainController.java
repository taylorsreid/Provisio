package provisio.api;

import org.springframework.http.*;
import provisio.api.models.requests.*;
import provisio.api.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
// @RequestMapping(path = "/Provisio/api")
@RestController
public class MainController {

    @Autowired
    private RegisterService registerService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private PricesService pricesService;

    @PostMapping(path = "/register", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    private @ResponseBody ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest){
        return registerService.register(registerRequest);
    }

    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    private @ResponseBody ResponseEntity<String> login(@RequestBody LoginRequest loginRequest){
        return loginService.login(loginRequest);
    }

    @PostMapping(path = "/reservations/new", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    private @ResponseBody ResponseEntity<String> createReservation(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestBody ReservationPostRequest reservationPostRequest
    ){
        return reservationService.postNewReservation(authorizationHeader, reservationPostRequest);
    }

    //authorization header is optional because it's not listed in the project requirements that the user be logged in to retrieve by reservation ID
    @PostMapping(path = "/reservations/getByReservationId", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    private @ResponseBody ResponseEntity<String> getReservationByReservationId(@RequestBody ReservationGetByReservationIdRequest reservationGetByReservationIdRequest){
        return reservationService.getByReservationId(reservationGetByReservationIdRequest);
    }

    @PostMapping(path = "/reservations/getByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    private @ResponseBody ResponseEntity<String> getReservationByUserId(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
            return reservationService.getByUserId(authorizationHeader);
    }

    @PostMapping(path = "/prices", produces = MediaType.APPLICATION_JSON_VALUE)
    private @ResponseBody ResponseEntity<String> getPriceForDateRange(@RequestBody ReservationPostRequest request){
        return pricesService.response(request);
    }

}
