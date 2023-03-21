package provisio.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import provisio.api.db.ConnectionManager;
import provisio.api.models.requests.ReservationGetRequest;
import provisio.api.models.requests.ReservationPostRequest;
import provisio.api.models.responses.GenericResponse;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

@Service
public class ReservationService {

    @Autowired
    private AuthorizationService authorizationService;

    public ResponseEntity<String> post(String authorizationHeader, ReservationPostRequest reservationPostRequest){

        //verify token
        if(authorizationHeader != null && authorizationService.verifyAuthorizationHeader(authorizationHeader)){

            try{
                Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement("INSERT INTO `reservations` (`customer_id`, `reservation_id`, `check_in`, `check_out`, `room_size`, `wifi`, `breakfast`, `parking`, `guests`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                ps.setString(1, new AuthorizationService().getCustomerIdFromAuthorizationHeader(authorizationHeader));
                ps.setString(2, UUID.randomUUID().toString());
                ps.setString(3, reservationPostRequest.getCheckIn());
                ps.setString(4, reservationPostRequest.getCheckOut());
                ps.setString(5, reservationPostRequest.getRoomSize());
                ps.setBoolean(6, reservationPostRequest.isWifi());
                ps.setBoolean(7, reservationPostRequest.isBreakfast());
                ps.setBoolean(8, reservationPostRequest.isParking());
                ps.setInt(9, reservationPostRequest.getGuests());
                ps.executeUpdate();

                return new ResponseEntity<>(new GenericResponse(true, "Your reservation is booked!").toString(), HttpStatus.OK);

            }
            catch (SQLException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }

        }
        else{
            return redirectToLogin();
        }
    }

    public ResponseEntity<String> get(String authorizationHeader, ReservationGetRequest reservationGetRequest){

        //verify token
        if(authorizationHeader != null && authorizationService.verifyAuthorizationHeader(authorizationHeader)){
            return null;
        }
        else{
            return redirectToLogin();
        }
    }

    private ResponseEntity<String> redirectToLogin(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("https://taylorsreid.github.io/Provisio/login.html"));
        return new ResponseEntity<>("Redirecting you to the login page...", httpHeaders, 302);
    }

}
