package provisio.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import provisio.api.db.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;

@Service
public class ChargesService {

    @Autowired
    private PricesService pricesService;

    private final String INSERT_STATEMENT = "INSERT INTO `charges` (`reservation_id`, `item_id`) VALUES (?, ?)";

    public boolean insertOne(String reservationId, String chargeName){
        try{
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(INSERT_STATEMENT);
            ps.setString(1, reservationId);
            ps.setInt(2, pricesService.selectItemIdFromItemName(chargeName));
            ps.executeUpdate();
            return true;
        }
        catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    public boolean insertMany(String reservationId, String chargeName, int times){
        try{
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(INSERT_STATEMENT);
            for (int i = 0; i < times; i++){
                ps.setString(1, reservationId);
                ps.setInt(2, pricesService.selectItemIdFromItemName(chargeName));
                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        }
        catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

//    public ArrayList

}
