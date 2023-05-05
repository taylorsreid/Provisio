package provisio.api.services;

import org.springframework.stereotype.Service;
import provisio.api.db.ConnectionManager;
import provisio.api.models.requests.ReservationPostRequest;
import java.sql.*;
import java.util.ArrayList;

@Service
public class ChargesService extends AbstractChargesPrices {

    protected void chargeEach(String chargeName, String reservationId, ArrayList<String> dateRange) throws SQLException, ClassNotFoundException {
        Connection conn = ConnectionManager.getConnection();
        PreparedStatement ps = conn.prepareStatement("INSERT INTO `room_charges` (`charge_prices_id`, `reservation_id`) VALUES (?, ?)");
        if (isPerNight(chargeName)){ //if the charge is a nightly charge, search database for each night's price, add it to batch, and execute
            for (String night : dateRange) {
                ps.setInt(1, getChargePricesId(chargeName, night));
                ps.setString(2, reservationId);
                ps.addBatch();
            }
            ps.executeBatch();
        }
        else { //if the charge is a one time charge, apply it to the first night due to database structure
            ps.setInt(1, getChargePricesId(chargeName, dateRange.get(0)));
            ps.setString(2, reservationId);
            ps.execute();
        }
    }

    protected void chargeForNewReservation(ReservationPostRequest request, String reservationId) throws SQLException, ClassNotFoundException {

        //get each date in reservation
        ArrayList<String> dateRange = dateService.getRange(request.getCheckIn(), request.getCheckOut());

        //charge for room
        chargeEach(request.getRoomSizeName(), reservationId, dateRange);

        //charge for amenities
        if (request.isWifi()){
            chargeEach("wifi", reservationId, dateRange); //won't actually charge for each night because wifi is listed as not per night in database
        }
        if (request.isBreakfast()){
            chargeEach("breakfast", reservationId, dateRange);
        }
        if (request.isParking()){
            chargeEach("parking", reservationId, dateRange);
        }

    }

}
