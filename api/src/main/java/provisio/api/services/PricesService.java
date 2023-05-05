package provisio.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import provisio.api.db.ConnectionManager;
import provisio.api.models.requests.ReservationPostRequest;
import provisio.api.models.responses.GenericResponse;
import provisio.api.models.responses.PricesResponse;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class PricesService extends AbstractChargesPrices {

    @Autowired
    private DateService dateService;

    protected BigDecimal getIndividualPrice(String chargeName, String date) throws SQLException, ClassNotFoundException {
        Connection conn = ConnectionManager.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT `price` FROM `charge_prices` WHERE (? BETWEEN `valid_from` AND `valid_until`) AND (`charge_names_id` = ?)");
        ps.setString(1, date);
        ps.setInt(2, getChargeNamesId(chargeName));
        ResultSet rs = ps.executeQuery();
        if (rs.next()){
            return rs.getBigDecimal("price");
        }
        else {
            throw new RuntimeException("Nothing found in database for \"" + chargeName + "\"");
        }
    }

    protected BigDecimal getPriceForDateRange(String chargeName, ArrayList<String> dateRange) throws SQLException, ClassNotFoundException {
        BigDecimal totalPrice = new BigDecimal(0);
        if (isPerNight(chargeName)){
            for (String night : dateRange) {
                totalPrice = totalPrice.add(getIndividualPrice(chargeName, night)); //BigDecimal is weird like that
            }
            return totalPrice;
        }
        else {
            return getIndividualPrice(chargeName, dateRange.get(0));
        }
    }

    public ResponseEntity<String> response(ReservationPostRequest request) {

        try {
            PricesResponse response = new PricesResponse();
            ArrayList<String> dateRange = dateService.getRange(request.getCheckIn(), request.getCheckOut());
            HashMap<String, BigDecimal> prices = new HashMap<>();
            BigDecimal grandTotal = new BigDecimal(0);

            //get room total
            prices.put(request.getRoomSizeName(), getPriceForDateRange(request.getRoomSizeName(), dateRange));

            //get wifi total
            if (request.isWifi()){
                prices.put("wifi", getPriceForDateRange("wifi", dateRange));
            }

            //get breakfast total
            if (request.isBreakfast()){
                prices.put("breakfast", getPriceForDateRange("breakfast", dateRange));
            }

            //get parking total
            if (request.isParking()){
                prices.put("parking", getPriceForDateRange("parking", dateRange));
            }

            //loop and create grand total
            for (BigDecimal total : prices.values()){
                grandTotal = grandTotal.add(total);
            }

            response.setPrices(prices);
            response.setTotal(grandTotal);
            response.setSuccess(true);

            return ResponseEntity.ok().body(response.toString());

        }
        catch (Exception ex){
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body(new GenericResponse(false, "An internal server error has occurred.").toString());
        }

    }

}
