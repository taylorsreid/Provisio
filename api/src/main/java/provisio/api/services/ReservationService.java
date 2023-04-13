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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ReservationService {

    final String UNAUTHORIZED_MESSAGE = "Invalid token, your session may have expired, please log in again.";

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private GuestsService guestsService;

    @Autowired
    private ChargesService chargesService;

    @Autowired
    private PricesService pricesService;

    public ResponseEntity<String> post(String authorizationHeader, ReservationPostRequest request){

        //verify token
        if(authorizationHeader != null && authorizationService.verifyAuthorizationHeader(authorizationHeader)){

            // Use Java Date objects for comparisons but send the date as a string because
            // Java and MySQL have compatibility issues between their date objects but strings always work.
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
            boolean isValidCheckInDate;
            boolean isValidDates;
            int lengthOfStay;
            try {
                Date checkInAsDate = isoFormat.parse(request.getCheckIn());
                Date checkOutAsDate = isoFormat.parse(request.getCheckOut());
                isValidCheckInDate = checkInAsDate.compareTo(Date.from(Instant.now().truncatedTo(ChronoUnit.DAYS))) >= 0; //checks that the check in date is today or greater
                isValidDates = checkOutAsDate.after(checkInAsDate); //checks that the check-out date is after the check in date
                lengthOfStay = (int) TimeUnit.DAYS.convert(checkOutAsDate.getTime() - checkInAsDate.getTime(), TimeUnit.MILLISECONDS);
            } catch (ParseException e) {
                return ResponseEntity.badRequest().body(new GenericResponse(false, "You must enter valid dates in ISO format.").toString());
            }

            if (isValidDates && isValidCheckInDate){

                try{
                    //generate random UUID as reservation ID
                    String reservationId = UUID.randomUUID().toString();

                    Connection conn = ConnectionManager.getConnection();

                    PreparedStatement hotelIdPs = conn.prepareStatement("SELECT `hotel_id` FROM `hotels` WHERE `hotel_name` = ?");
                    hotelIdPs.setString(1, request.getHotelName());
                    ResultSet hotelIdRs = hotelIdPs.executeQuery();
                    hotelIdRs.next();
                    int hotelId = hotelIdRs.getInt("hotel_id");

                    int roomSizeId = pricesService.selectItemIdFromItemName(request.getRoomSizeName());

                    PreparedStatement reservationsPs = conn.prepareStatement("INSERT INTO `reservations` (`reservation_id`, `user_id`, `hotel_id`, `check_in`, `check_out`, `room_size_id`, `wifi`, `breakfast`, `parking`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

                    //the customer ID is stored in the signed JWT so that it can't be forged
                    reservationsPs.setString(1, reservationId);
                    reservationsPs.setString(2, authorizationService.getUserIdFromAuthorizationHeader(authorizationHeader));
                    reservationsPs.setInt(3, hotelId);
                    reservationsPs.setString(4, request.getCheckIn());
                    reservationsPs.setString(5, request.getCheckOut());
                    reservationsPs.setInt(6, roomSizeId);
                    reservationsPs.setBoolean(7, request.isWifi());
                    reservationsPs.setBoolean(8, request.isBreakfast());
                    reservationsPs.setBoolean(9, request.isParking());
                    reservationsPs.executeUpdate();

                    //insert guests
                    guestsService.insertMany(reservationId, request.getGuests());

                    //insert charges
                    chargesService.insertMany(reservationId, request.getRoomSizeName(), lengthOfStay);
                    if (request.isWifi()){
                        chargesService.insertOne(reservationId, "wifi"); //flat rate so insert one
                    }
                    if (request.isBreakfast()){
                        chargesService.insertMany(reservationId, "breakfast", lengthOfStay);
                    }
                    if (request.isParking()){
                        chargesService.insertMany(reservationId, "parking", lengthOfStay);
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

                //close db connection and return response
                conn.close();
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
