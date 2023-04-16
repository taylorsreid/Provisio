package provisio.api.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import provisio.api.db.ConnectionManager;
import provisio.api.models.PricesRequest;
import provisio.api.models.responses.GenericResponse;
import provisio.api.models.responses.PricesResponse;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;


@Service
public class PricesService {

    public int selectItemIdFromItemName(String itemName) throws ClassNotFoundException, SQLException {
        Connection conn = ConnectionManager.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT `item_id` FROM `prices` WHERE `item_name` = ?");
        ps.setString(1, itemName);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt("item_id");
    }

    public String selectItemNameFromItemId(int itemId) throws ClassNotFoundException, SQLException {
        Connection conn = ConnectionManager.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT `item_id` FROM `prices` WHERE `item_name` = ?");
        ps.setInt(1, itemId);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getString("item_name");
    }

    //TODO: REFACTOR TO BATCH
    public ResponseEntity<String> getPrices(PricesRequest request){
        try{
            Connection conn = ConnectionManager.getConnection();
//            PricesResponse response = new PricesResponse();
            HashMap<String, BigDecimal> hm = new HashMap<>();
            for (String item : request.getItems() ) {
                try {
                    PreparedStatement ps = conn.prepareStatement("SELECT `item_price` FROM `prices` WHERE `item_name` = ?");
                    ps.setString(1, item);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()){
                        hm.put(item, rs.getBigDecimal("item_price"));
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return ResponseEntity.ok(new PricesResponse(true, hm).toString());
        }
        catch (ClassNotFoundException ex){
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body(new GenericResponse(false, "An internal server error has occurred.").toString());
        }
    }

}
