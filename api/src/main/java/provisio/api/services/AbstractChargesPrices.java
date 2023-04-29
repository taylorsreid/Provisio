package provisio.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import provisio.api.db.ConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public abstract class AbstractChargesPrices {

    @Autowired
    protected DateService dateService;

    protected Connection conn = ConnectionManager.getConnection();

    public AbstractChargesPrices() throws ClassNotFoundException {}

    protected int getChargeNamesId(String chargeName) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT `charge_names_id` FROM `charge_names` WHERE `name` = ?");
        ps.setString(1, chargeName);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt("charge_names_id");
    }

    protected int getChargePricesId(String chargeName, String date) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT `charge_prices_id` FROM `charge_prices` WHERE (? BETWEEN `valid_from` AND `valid_until`) AND (`charge_names_id` = ?)");
        ps.setString(1, date);
        ps.setInt(2, getChargeNamesId(chargeName));
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt("charge_prices_id");

    }

    protected boolean isPerNight(String chargeName) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT `per_night` FROM `charge_names` WHERE `name` = ?");
        ps.setString(1, chargeName);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getBoolean("per_night");
    }

}
