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

    private final Connection conn = ConnectionManager.getConnection();

    public GuestsService() throws ClassNotFoundException {}

    protected void insert(String reservationId, ArrayList<Guest> guests) {
        try{
            PreparedStatement ps = conn.prepareStatement("INSERT INTO `guests` (`reservation_id`, `guest_first_name`, `guest_last_name`) VALUES (?, ?, ?)");
            for (Guest guest : guests) {
                ps.setString(1, reservationId);
                ps.setString(2, guest.getFirstName());
                ps.setString(3, guest.getLastName());
                ps.addBatch();
            }
            ps.executeBatch();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    protected void insert(String reservationId, Guest guest) {
        ArrayList<Guest> guestArrayList = new ArrayList<>(1);
        guestArrayList.add(guest);
        insert(reservationId, guestArrayList);
    }

    protected void insert(String reservationId, String firstName, String lastName){
        ArrayList<Guest> guestArrayList = new ArrayList<>(1);
        guestArrayList.add(new Guest(firstName, lastName));
        insert(reservationId, guestArrayList);
    }

    protected ArrayList<Guest> selectMany(String reservationId) throws ClassNotFoundException, SQLException {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT `guest_first_name`, `guest_last_name` FROM `guests` WHERE `reservation_id` = ?");
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
