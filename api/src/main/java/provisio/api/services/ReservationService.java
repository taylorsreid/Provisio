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

    final String unauthorizedMessage = "BAD TOKEN";

    @Autowired
    private AuthorizationService authorizationService;

    public ResponseEntity<String> post(String authorizationHeader, ReservationPostRequest reservationPostRequest){

        //verify token
        if(authorizationHeader != null && authorizationService.verifyAuthorizationHeader(authorizationHeader)){

            try{

                //generate random UUID as reservation ID
                String reservationId = UUID.randomUUID().toString();

                Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement("INSERT INTO `reservations` (`customer_id`, `reservation_id`, `hotel_id`, `check_in`, `check_out`, `room_size_id`, `wifi`, `breakfast`, `parking`, `guests`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

                //the customer ID is stored in the JWT so that it can't be forged
                ps.setString(1, authorizationService.getCustomerIdFromAuthorizationHeader(authorizationHeader));
                ps.setString(2, reservationId);
                ps.setInt(3, reservationPostRequest.getHotelId());
                ps.setString(4, reservationPostRequest.getCheckIn());
                ps.setString(5, reservationPostRequest.getCheckOut());
                ps.setInt(6, reservationPostRequest.getRoomSizeId());
                ps.setBoolean(7, reservationPostRequest.isWifi());
                ps.setBoolean(8, reservationPostRequest.isBreakfast());
                ps.setBoolean(9, reservationPostRequest.isParking());
                ps.setInt(10, reservationPostRequest.getGuests());
                ps.executeUpdate();

                conn.close();

                //for watching the application run
                System.out.println("Customer " + authorizationService.getCustomerIdFromAuthorizationHeader(authorizationHeader) + " has made a reservation.");

                return new ResponseEntity<>(new GenericResponse(true, "Reservation " + reservationId + " has been booked!").toString(), HttpStatus.OK);

            }
            catch (SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
                return ResponseEntity.internalServerError().body(new GenericResponse(false, "An internal server error has occurred.").toString());
            }

        }
        else{
            System.out.println("Authorization header: " + authorizationHeader);
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
