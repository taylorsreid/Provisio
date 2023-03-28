package provisio.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import provisio.api.db.ConnectionManager;
import provisio.api.models.Guest;
import provisio.api.models.requests.ReservationGetByUserIdRequest;
import provisio.api.models.requests.ReservationGetByReservationIdRequest;
import provisio.api.models.requests.ReservationPostRequest;
import provisio.api.models.responses.GenericResponse;
import provisio.api.models.responses.ReservationGetByReservationIdResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@Service
public class ReservationService {

    final String UNAUTHORIZED_MESSAGE = "Invalid token, your session may have expired, please log in again.";

    @Autowired
    private AuthorizationService authorizationService;

    public ResponseEntity<String> post(String authorizationHeader, ReservationPostRequest reservationPostRequest){

        //verify token
        if(authorizationHeader != null && authorizationService.verifyAuthorizationHeader(authorizationHeader)){

            // Use Java Date objects for comparisons but send the date as a string because
            // Java and MySQL have compatibility issues between their date objects but strings always work.
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
            boolean isValidCheckInDate;
            boolean isValidDates;
            try {
                Date checkInAsDate = isoFormat.parse(reservationPostRequest.getCheckIn());
                Date checkOutAsDate = isoFormat.parse(reservationPostRequest.getCheckOut());
                isValidCheckInDate = checkInAsDate.compareTo(Date.from(Instant.now().truncatedTo(ChronoUnit.DAYS))) >= 0; //checks that the check in date is today or greater
                isValidDates = checkOutAsDate.after(checkInAsDate); //checks that the check out date is after the check in date
            } catch (ParseException e) {
                return ResponseEntity.badRequest().body(new GenericResponse(false, "You must enter valid dates in ISO format.").toString());
            }

            if (isValidDates && isValidCheckInDate){

                try{
                    //generate random UUID as reservation ID
                    String reservationId = UUID.randomUUID().toString();

                    Connection conn = ConnectionManager.getConnection();

                    PreparedStatement locationIdPs = conn.prepareStatement("SELECT `location_id` FROM `locations` WHERE `location_name` = ?");
                    locationIdPs.setString(1, reservationPostRequest.getLocationName());
                    ResultSet locationIdRs = locationIdPs.executeQuery();
                    locationIdRs.next();
                    int locationId = locationIdRs.getInt("location_id");

                    PreparedStatement reservationsPs = conn.prepareStatement("INSERT INTO `reservations` (`reservation_id`, `user_id`, `location_id`, `check_in`, `check_out`, `room_size_id`, `wifi`, `breakfast`, `parking`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

                    //the customer ID is stored in the JWT so that it can't be forged
                    reservationsPs.setString(1, reservationId);
                    reservationsPs.setString(2, authorizationService.getUserIdFromAuthorizationHeader(authorizationHeader));
                    reservationsPs.setInt(3, locationId);
                    reservationsPs.setString(4, reservationPostRequest.getCheckIn());
                    reservationsPs.setString(5, reservationPostRequest.getCheckOut());
                    reservationsPs.setInt(6, reservationPostRequest.getRoomSizeId());
                    reservationsPs.setBoolean(7, reservationPostRequest.isWifi());
                    reservationsPs.setBoolean(8, reservationPostRequest.isBreakfast());
                    reservationsPs.setBoolean(9, reservationPostRequest.isParking());
                    reservationsPs.executeUpdate();

                    for (Guest guest : reservationPostRequest.getGuests()) {
                        PreparedStatement guestsPs = conn.prepareStatement("INSERT INTO `guests` (`reservation_id`, `first_name`, `last_name`) VALUES (?, ?, ?)");
                        guestsPs.setString(1, reservationId);
                        guestsPs.setString(2, guest.getFirstName());
                        guestsPs.setString(3, guest.getLastName());
                        guestsPs.executeUpdate();
                    }

                    conn.close();

                    //for watching the application run
                    System.out.println("User " + authorizationService.getUserIdFromAuthorizationHeader(authorizationHeader) + " has made a reservation.");

                    return new ResponseEntity<>(new GenericResponse(true, "Reservation " + reservationId + " has been booked!").toString(), HttpStatus.OK);

                }
                catch (SQLException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                    return ResponseEntity.internalServerError().body(new GenericResponse(false, "An internal server error has occurred.").toString());
                }

            }
            else {
                return ResponseEntity.badRequest().body(new GenericResponse(false, "Your check out date must be after your check in date and your check in date must be at least today.").toString());
            }

        }
        else{
            return new ResponseEntity<>(new GenericResponse(false, UNAUTHORIZED_MESSAGE).toString(), HttpStatus.UNAUTHORIZED);
        }

    }

    //The page should include a field to search by reservation ID and display a summary of the reservation.
    // List the location, room size, number of guests, amenities, and check-in/check-out dates.
    //TODO: determine if the user needs to be logged in or not to retrieve reservations, if so, remove authorization header argument and if statement
    public ResponseEntity<String> getByReservationId(String authorizationHeader, ReservationGetByReservationIdRequest reservationGetByReservationIdRequest){

        //verify token
        if(authorizationHeader != null && authorizationService.verifyAuthorizationHeader(authorizationHeader)){
            try {
                Connection conn = ConnectionManager.getConnection();

                //select from inner joined view
                PreparedStatement ps = conn.prepareStatement(
                """
                    SELECT `location_name`, `room_size_name`, `wifi`, `breakfast`, `parking`, `check_in`, `check_out`
                    FROM `reservations_view`
                    WHERE `reservations_view`.`reservation_id` = ?
                    """);
                ps.setString(1, reservationGetByReservationIdRequest.getReservationId());
                ResultSet resultSetReservation = ps.executeQuery();

                ps = conn.prepareStatement("SELECT `first_name`, `last_name` FROM `guests` WHERE `reservation_id` = ?");
                ps.setString(1, reservationGetByReservationIdRequest.getReservationId());
                ResultSet resultSetGuests = ps.executeQuery();

                ArrayList<Guest> arGuests = new ArrayList<>();

                while(resultSetGuests.next()){
                    arGuests.add(new Guest(resultSetGuests.getString("first_name"), resultSetGuests.getString("last_name")));
                }

                ReservationGetByReservationIdResponse rsResponse = new ReservationGetByReservationIdResponse();
                if (resultSetReservation.next()){
                    rsResponse.setSuccess(true);
                    rsResponse.setLocationName(resultSetReservation.getString("location_name"));
                    rsResponse.setRoomSizeName(resultSetReservation.getString("room_size_name"));
                    rsResponse.setWifi(resultSetReservation.getBoolean("wifi"));
                    rsResponse.setBreakfast(resultSetReservation.getBoolean("breakfast"));
                    rsResponse.setParking(resultSetReservation.getBoolean("parking"));
                    rsResponse.setCheckIn(resultSetReservation.getDate("check_in").toString());
                    rsResponse.setCheckOut(resultSetReservation.getDate("check_out").toString());
                    rsResponse.setGuests(arGuests);
                    return ResponseEntity.ok(rsResponse.toString());
                }
                else {
                    return ResponseEntity.ok(new GenericResponse(false, "No results for reservation ID" + reservationGetByReservationIdRequest.getReservationId()).toString());
                }

            }
            catch (SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
                return ResponseEntity.internalServerError().body(new GenericResponse(false, "An internal server error has occurred.").toString());
            }
        }
        else{
            return new ResponseEntity<>(new GenericResponse(false, UNAUTHORIZED_MESSAGE).toString(), HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<String> getByCustomerId(String authorizationHeader, ReservationGetByUserIdRequest reservationGetByUserIdRequest){
        return null;
    }

}
