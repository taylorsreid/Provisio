package provisio.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import provisio.api.db.ConnectionManager;
import provisio.api.models.requests.ReservationGetRequest;
import provisio.api.models.requests.ReservationPostRequest;
import provisio.api.models.responses.GenericResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

@Service
public class ReservationService {

    final String unauthorizedMessage = "You must be logged in to do that!";

    @Autowired
    private AuthorizationService authorizationService;

    public ResponseEntity<String> post(String authorizationHeader, ReservationPostRequest reservationPostRequest){

        //verify token
        if(authorizationHeader != null && authorizationService.verifyAuthorizationHeader(authorizationHeader)){

            try{

                String reservationId = UUID.randomUUID().toString();

                Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement("INSERT INTO `reservations` (`customer_id`, `reservation_id`, `check_in`, `check_out`, `room_size`, `wifi`, `breakfast`, `parking`, `guests`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                ps.setString(1, new AuthorizationService().getCustomerIdFromAuthorizationHeader(authorizationHeader));
                ps.setString(2, reservationId);
                ps.setString(3, reservationPostRequest.getCheckIn());
                ps.setString(4, reservationPostRequest.getCheckOut());
                ps.setString(5, reservationPostRequest.getRoomSize());
                ps.setBoolean(6, reservationPostRequest.isWifi());
                ps.setBoolean(7, reservationPostRequest.isBreakfast());
                ps.setBoolean(8, reservationPostRequest.isParking());
                ps.setInt(9, reservationPostRequest.getGuests());
                ps.executeUpdate();

                conn.close();

                System.out.println("Customer " + authorizationService.getCustomerIdFromAuthorizationHeader(authorizationHeader) + " has made a reservation.");

                return new ResponseEntity<>(new GenericResponse(true, "Reservation " + reservationId + " is booked!").toString(), HttpStatus.OK);

            }
            catch (SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
                return ResponseEntity.internalServerError().body(new GenericResponse(false, "An internal server error has occurred.").toString());
            }

        }
        else{
            return new ResponseEntity<>(new GenericResponse(false, unauthorizedMessage).toString(), HttpStatus.UNAUTHORIZED);
        }

    }

    //TODO: determine if the user needs to be logged in or not to retrieve reservations, if so, remove authorization header argument and if statement
    public ResponseEntity<String> get(String authorizationHeader, ReservationGetRequest reservationGetRequest){

        //verify token
        if(authorizationHeader != null && authorizationService.verifyAuthorizationHeader(authorizationHeader)){
            return null;
        }
        return null;
    }

}
