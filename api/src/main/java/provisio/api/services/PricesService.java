package provisio.api.services;

import org.springframework.stereotype.Service;
import provisio.api.db.ConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


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

}
