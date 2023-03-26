package provisio.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import provisio.api.db.ConnectionManager;
import provisio.api.models.Guest;
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

    //TODO add check in and check out date verification
    public ResponseEntity<String> post(String authorizationHeader, ReservationPostRequest reservationPostRequest){

        //verify token
        if(authorizationHeader != null && authorizationService.verifyAuthorizationHeader(authorizationHeader)){

            try{
                //generate random UUID as reservation ID
                String reservationId = UUID.randomUUID().toString();

                Connection conn = ConnectionManager.getConnection();
                PreparedStatement resPs = conn.prepareStatement("INSERT INTO `reservations` (`reservation_id`, `customer_id`, `hotel_id`, `check_in`, `check_out`, `room_size_id`, `wifi`, `breakfast`, `parking`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

//                conn.setAutoCommit(false);

                //the customer ID is stored in the JWT so that it can't be forged
                resPs.setString(1, reservationId);
                resPs.setString(2, authorizationService.getCustomerIdFromAuthorizationHeader(authorizationHeader));
                resPs.setInt(3, reservationPostRequest.getHotelId());
                resPs.setString(4, reservationPostRequest.getCheckIn());
                resPs.setString(5, reservationPostRequest.getCheckOut());
                resPs.setInt(6, reservationPostRequest.getRoomSizeId());
                resPs.setBoolean(7, reservationPostRequest.isWifi());
                resPs.setBoolean(8, reservationPostRequest.isBreakfast());
                resPs.setBoolean(9, reservationPostRequest.isParking());
                resPs.executeUpdate();

//                conn.commit();

                for (Guest guest : reservationPostRequest.getGuests()) {
                    PreparedStatement guestsPs = conn.prepareStatement("INSERT INTO `guests` (`reservation_id`, `first_name`, `last_name`) VALUES (?, ?, ?)");
                    guestsPs.setString(1, reservationId);
                    guestsPs.setString(2, guest.getFirstName());
                    guestsPs.setString(3, guest.getLastName());
                    guestsPs.executeUpdate();
                }

//                conn.commit();
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
