package provisio.api;

import org.springframework.http.*;
import provisio.api.models.requests.*;
import provisio.api.models.responses.GenericResponse;
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
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestBody ReservationPostRequest reservationPostRequest
    ){
        return reservationService.post(authorizationHeader, reservationPostRequest);
    }

    //authorization header is optional for now because it's not listed in the project requirements that the user be
    //logged in to retrieve by reservation ID
    @GetMapping(path = "/reservation", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> getReservation(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,
            @RequestBody(required = false) ReservationGetByReservationIdRequest reservationGetByReservationIdRequest,
            @RequestBody(required = false) ReservationGetByUserIdRequest reservationGetByUserIdRequest
            ){

        if (reservationGetByUserIdRequest != null) {
            return reservationService.getByCustomerId(authorizationHeader, reservationGetByUserIdRequest);
        }
        if (reservationGetByReservationIdRequest != null) {
            return reservationService.getByReservationId(authorizationHeader, reservationGetByReservationIdRequest);
        }

        return ResponseEntity.badRequest().body(new GenericResponse(false, "BAD REQUEST").toString());

    }

//    @RestControllerAdvice
//    public static class ControllerExceptionHandler {
//
//        @ExceptionHandler(MissingRequestHeaderException.class)
//        public void handleException(MissingRequestHeaderException ex) {
//            ex.printStackTrace();
//        }
//    }

}