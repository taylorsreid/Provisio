package provisio.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import provisio.api.db.ConnectionManager;
import provisio.api.models.Guest;
import provisio.api.models.ReservationByUserId;
import provisio.api.models.requests.ReservationGetByReservationIdRequest;
import provisio.api.models.requests.ReservationPostRequest;
import provisio.api.models.responses.GenericResponse;
import provisio.api.models.responses.ReservationGetByReservationIdResponse;
import provisio.api.models.responses.ReservationGetByUserIdResponse;

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
                    locationIdPs.setString(1, reservationPostRequest.getLocation());
                    ResultSet locationIdRs = locationIdPs.executeQuery();
                    locationIdRs.next();
                    int locationId = locationIdRs.getInt("location_id");

                    PreparedStatement roomSizeIdPs = conn.prepareStatement("SELECT `room_size_id` FROM `room_sizes` WHERE `room_size_name` = ?");
                    roomSizeIdPs.setString(1, reservationPostRequest.getRoomSize());
                    ResultSet roomSizeIdRs = roomSizeIdPs.executeQuery();
                    roomSizeIdRs.next();
                    int roomSizeId = roomSizeIdRs.getInt("room_size_id");

                    PreparedStatement reservationsPs = conn.prepareStatement("INSERT INTO `reservations` (`reservation_id`, `user_id`, `location_id`, `check_in`, `check_out`, `room_size_id`, `wifi`, `breakfast`, `parking`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

                    //the customer ID is stored in the JWT so that it can't be forged
                    reservationsPs.setString(1, reservationId);
                    reservationsPs.setString(2, authorizationService.getUserIdFromAuthorizationHeader(authorizationHeader));
                    reservationsPs.setInt(3, locationId);
                    reservationsPs.setString(4, reservationPostRequest.getCheckIn());
                    reservationsPs.setString(5, reservationPostRequest.getCheckOut());
                    reservationsPs.setInt(6, roomSizeId);
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

    //As a group we have determined that the user should not need to be logged in if they are looking up a reservation
    //by reservation ID, since that is how other companies have their websites setup.
    public ResponseEntity<String> getByReservationId(ReservationGetByReservationIdRequest request){
            try {
                Connection conn = ConnectionManager.getConnection();

                //select from view because the actual table only contains ID numbers linking to other tables,
                //whereas the view contains the ID number's corresponding value
                PreparedStatement ps = conn.prepareStatement(
                """
                    SELECT `location_name`, `room_size_name`, `wifi`, `breakfast`, `parking`, `check_in`, `check_out`
                    FROM `reservations_view`
                    WHERE `reservations_view`.`reservation_id` = ?
                    """);
                ps.setString(1, request.getReservationId());
                ResultSet resultSetReservation = ps.executeQuery();

                ps = conn.prepareStatement("SELECT `first_name`, `last_name` FROM `guests` WHERE `reservation_id` = ?");
                ps.setString(1, request.getReservationId());
                ResultSet resultSetGuests = ps.executeQuery();

                ArrayList<Guest> arGuests = new ArrayList<>();

                while(resultSetGuests.next()){
                    arGuests.add(new Guest(resultSetGuests.getString("first_name"), resultSetGuests.getString("last_name")));
                }

                ReservationGetByReservationIdResponse response = new ReservationGetByReservationIdResponse();
                if (resultSetReservation.next()){
                    response.setSuccess(true);
                    response.setLocation(resultSetReservation.getString("location_name"));
                    response.setRoomSize(resultSetReservation.getString("room_size_name"));
                    response.setWifi(resultSetReservation.getBoolean("wifi"));
                    response.setBreakfast(resultSetReservation.getBoolean("breakfast"));
                    response.setParking(resultSetReservation.getBoolean("parking"));
                    response.setCheckIn(resultSetReservation.getDate("check_in").toString());
                    response.setCheckOut(resultSetReservation.getDate("check_out").toString());
                    response.setGuests(arGuests);
                    return ResponseEntity.ok(response.toString());
                }
                else {
                    return ResponseEntity.ok(new GenericResponse(false, "No results for reservation ID " + request.getReservationId()).toString());
                }

            }
            catch (SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
                return ResponseEntity.internalServerError().body(new GenericResponse(false, "An internal server error has occurred.").toString());
            }
        }

    //the user must be logged in to retrieve all reservation information saved in the DB for them
    public ResponseEntity<String> getByUserId(String authorizationHeader){
        //verify token and that the claimed user ID in the request matches the authorization header's user ID
        if(authorizationHeader != null && authorizationService.verifyAuthorizationHeader(authorizationHeader)) {
            try {

                String userId = authorizationService.getUserIdFromAuthorizationHeader(authorizationHeader);

                Connection conn = ConnectionManager.getConnection();

                //select from view because the actual table only contains ID numbers linking to other tables,
                //whereas the view contains the ID number's corresponding value
                PreparedStatement ps = conn.prepareStatement(
                        """
                            SELECT `reservation_id`, `location_name`, `check_in`, `check_out`, `points_earned`
                            FROM `reservations_view`
                            WHERE `reservations_view`.`user_id` = ?
                            """);
                ps.setString(1, userId);
                ResultSet reservationsRs = ps.executeQuery();

                ReservationGetByUserIdResponse response = new ReservationGetByUserIdResponse();
                ArrayList<ReservationByUserId> reservationArrayList = new ArrayList<>();

                while (reservationsRs.next()){
                    response.setSuccess(true);
                    ReservationByUserId reservation = new ReservationByUserId();
                    reservation.setReservationId(reservationsRs.getString("reservation_id"));
                    reservation.setLocation(reservationsRs.getString("location_name"));
                    reservation.setCheckIn(reservationsRs.getString("check_in"));
                    reservation.setCheckOut(reservationsRs.getString("check_out"));
                    reservation.setPointsEarned(reservationsRs.getInt("points_earned"));
                    reservationArrayList.add(reservation);
                }

                response.setReservations(reservationArrayList);

                ps = conn.prepareStatement("SELECT SUM(`points_earned`) AS 'total_points_earned' FROM `reservations_view` WHERE `user_id` = ?");
                ps.setString(1, userId);
                ResultSet pointsRs = ps.executeQuery();
                if (pointsRs.next()){
                    response.setTotalPointsEarned(pointsRs.getInt("total_points_earned"));
                }

                return ResponseEntity.ok(response.toString());
            }
            catch (SQLException | ClassNotFoundException ex){
                ex.printStackTrace();
                return ResponseEntity.internalServerError().body(new GenericResponse(false, "An internal server error has occurred.").toString());
            }
        }
        else{
            return new ResponseEntity<>(new GenericResponse(false, UNAUTHORIZED_MESSAGE).toString(), HttpStatus.UNAUTHORIZED);
        }
    }

}
