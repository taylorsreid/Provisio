package provisio.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import provisio.api.db.ConnectionManager;
import provisio.api.models.Guest;
import provisio.api.models.IndividualReservation;
import provisio.api.models.requests.ReservationGetByReservationIdRequest;
import provisio.api.models.requests.ReservationPostRequest;
import provisio.api.models.responses.GenericResponse;
import provisio.api.models.responses.ReservationGetByReservationIdResponse;
import provisio.api.models.responses.ReservationGetByUserIdResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class ReservationService {

    final String UNAUTHORIZED_MESSAGE = "Invalid token, your session may have expired, please log in again.";

    final String INTERNAL_SERVER_ERROR = "An internal server error has occurred.";

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private GuestsService guestsService;

    @Autowired
    private ChargesService chargesService;

    @Autowired
    private DateService dateService;

    Connection conn = ConnectionManager.getConnection();

    public ReservationService() throws ClassNotFoundException {}

    public ResponseEntity<String> postNewReservation(String authorizationHeader, ReservationPostRequest request){

        //verify token
        if(authorizationHeader != null && authorizationService.verifyAuthorizationHeader(authorizationHeader)){

            if (dateService.validateDates(request.getCheckIn(), request.getCheckOut())){

                try{
                    //generate random UUID as reservation ID
                    String reservationId = UUID.randomUUID().toString();

                    //reusable prepared statement and result set
                    PreparedStatement ps;
                    ResultSet rs;

                    //get hotel id
                    ps = conn.prepareStatement("SELECT `hotel_id` FROM `hotels` WHERE `hotel_name` = ?");
                    ps.setString(1, request.getHotelName());
                    rs = ps.executeQuery();
                    rs.next();
                    int hotelId = rs.getInt("hotel_id");

                    //get chargeable names id
                    ps = conn.prepareStatement("SELECT `charge_names_id` FROM `charge_names` WHERE `name` = ?");
                    ps.setString(1, request.getRoomSizeName());
                    rs = ps.executeQuery();
                    rs.next();
                    int chargeableNamesId = rs.getInt("charge_names_id");

                    //getChargePricesId into reservations table
                    ps = conn.prepareStatement("INSERT INTO `reservations` (`reservation_id`, `user_id`, `hotel_id`, `check_in`, `check_out`, `room_size_id`, `wifi`, `breakfast`, `parking`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    ps.setString(1, reservationId);
                    ps.setString(2, authorizationService.getUserIdFromAuthorizationHeader(authorizationHeader)); //the customer ID is stored in the signed JWT so that it can't be forged
                    ps.setInt(3, hotelId);
                    ps.setString(4, request.getCheckIn());
                    ps.setString(5, request.getCheckOut());
                    ps.setInt(6, chargeableNamesId);
                    ps.setBoolean(7, request.isWifi());
                    ps.setBoolean(8, request.isBreakfast());
                    ps.setBoolean(9, request.isParking());
                    ps.executeUpdate();

                    //getChargePricesId into guests table
                    guestsService.insert(reservationId, request.getGuests());

                    //insert charges for reservation
                    chargesService.chargeForNewReservation(request, reservationId);

                    //for watching the application run
                    System.out.println("User " + authorizationService.getUserIdFromAuthorizationHeader(authorizationHeader) + " has made a reservation.");

                    return new ResponseEntity<>(new GenericResponse(true, "Reservation " + reservationId + " has been booked!").toString(), HttpStatus.OK);

                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    return ResponseEntity.internalServerError().body(new GenericResponse(false, INTERNAL_SERVER_ERROR).toString());
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

                //select from view because the actual table only contains ID numbers linking to other tables,
                //whereas the view contains the ID number's corresponding value
                PreparedStatement ps = conn.prepareStatement(
                """
                    SELECT `hotel_name`, `room_size_name`, `wifi`, `breakfast`, `parking`, `check_in`, `check_out`
                    FROM `reservations_view`
                    WHERE `reservations_view`.`reservation_id` = ?
                    """);
                ps.setString(1, request.getReservationId());
                ResultSet rs = ps.executeQuery();

                ArrayList<Guest> guests = guestsService.selectMany(request.getReservationId());

                ReservationGetByReservationIdResponse response = new ReservationGetByReservationIdResponse();
                if (rs.next()){
                    response.setSuccess(true);
                    response.setHotelName(rs.getString("hotel_name"));
                    response.setRoomSizeName(rs.getString("room_size_name"));
                    response.setGuests(guests);
                    response.setWifi(rs.getBoolean("wifi"));
                    response.setBreakfast(rs.getBoolean("breakfast"));
                    response.setParking(rs.getBoolean("parking"));
                    response.setCheckIn(rs.getDate("check_in").toString());
                    response.setCheckOut(rs.getDate("check_out").toString());

                    System.out.println("Reservation " + request.getReservationId() + " has been looked up.");
                    return ResponseEntity.ok(response.toString());
                }
                else {
                    return ResponseEntity.ok(new GenericResponse(false, "No results for reservation ID " + request.getReservationId()).toString());
                }

            }
            catch (SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
                return ResponseEntity.internalServerError().body(new GenericResponse(false, INTERNAL_SERVER_ERROR).toString());
            }
        }

    //the user must be logged in to retrieve all reservation information saved in the DB for them
    public ResponseEntity<String> getByUserId(String authorizationHeader){
        //verify token and that the claimed user ID in the request matches the authorization header's user ID
        if(authorizationHeader != null && authorizationService.verifyAuthorizationHeader(authorizationHeader)) {
            try {

                String userId = authorizationService.getUserIdFromAuthorizationHeader(authorizationHeader);

                //select from view because the actual table only contains ID numbers linking to other tables,
                //whereas the view contains the ID number's corresponding value
                PreparedStatement ps = conn.prepareStatement(
                        """
                            SELECT `reservation_id`, `hotel_name`, `check_in`, `check_out`, `points_earned`
                            FROM `reservations_view`
                            WHERE `reservations_view`.`user_id` = ?
                            """);
                ps.setString(1, userId);
                ResultSet reservationsRs = ps.executeQuery();

                //create response objects to be added to
                ReservationGetByUserIdResponse response = new ReservationGetByUserIdResponse();
                ArrayList<IndividualReservation> individualReservationArrayList = new ArrayList<>();

                //loop through result set and generate individual reservation objects then add them to the array list
                //that will be returned inside the ReservationGetByUserIdResponse object
                while (reservationsRs.next()){
                    response.setSuccess(true);
                    IndividualReservation individualReservation = new IndividualReservation();
                    individualReservation.setReservationId(reservationsRs.getString("reservation_id"));
                    individualReservation.setHotelName(reservationsRs.getString("hotel_name"));
                    individualReservation.setCheckIn(reservationsRs.getString("check_in"));
                    individualReservation.setCheckOut(reservationsRs.getString("check_out"));
                    individualReservation.setPointsEarned(reservationsRs.getInt("points_earned"));
                    individualReservationArrayList.add(individualReservation);
                }

                //add array list of reservations to ReservationGetByUserIdResponse object
                response.setReservations(individualReservationArrayList);

                ps = conn.prepareStatement("SELECT SUM(`points_earned`) AS 'total_points_earned' FROM `reservations_view` WHERE `user_id` = ?");
                ps.setString(1, userId);
                ResultSet pointsRs = ps.executeQuery();
                if (pointsRs.next()){
                    response.setTotalPointsEarned(pointsRs.getInt("total_points_earned"));
                }

                System.out.println("User " + authorizationService.getUserIdFromAuthorizationHeader(authorizationHeader) + " has looked up all of their reservations.");
                return ResponseEntity.ok(response.toString());
            }
            catch (SQLException ex){
                ex.printStackTrace();
                return ResponseEntity.internalServerError().body(new GenericResponse(false, INTERNAL_SERVER_ERROR).toString());
            }
        }
        else{
            return new ResponseEntity<>(new GenericResponse(false, UNAUTHORIZED_MESSAGE).toString(), HttpStatus.UNAUTHORIZED);
        }
    }

}
