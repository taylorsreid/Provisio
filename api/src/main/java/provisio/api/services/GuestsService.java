package provisio.api.services;

import org.springframework.stereotype.Service;
import provisio.api.db.ConnectionManager;
import provisio.api.models.Guest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Service
public class GuestsService {

    private final String INSERT_STATEMENT = "INSERT INTO `guests` (`reservation_id`, `guest_first_name`, `guest_last_name`) VALUES (?, ?, ?)";
    private final String SELECT_MANY_STATEMENT = "SELECT `guest_first_name`, `guest_last_name` FROM `guests` WHERE `reservation_id` = ?";

    public boolean insertMany(String reservationId, ArrayList<Guest> guests) {
        try{
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(INSERT_STATEMENT);
            for (Guest guest : guests) {
                ps.setString(1, reservationId);
                ps.setString(2, guest.getFirstName());
                ps.setString(3, guest.getLastName());
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

    public ArrayList<Guest> selectMany(String reservationId) throws ClassNotFoundException, SQLException {
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(SELECT_MANY_STATEMENT);
            ps.setString(1, reservationId);
            ResultSet rs = ps.executeQuery();
            ArrayList<Guest> guests = new ArrayList<>();
            while(rs.next()){
                guests.add(new Guest(rs.getString("guest_first_name"), rs.getString("guest_last_name")));
            }
            return guests;
        }
        catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}
