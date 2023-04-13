package provisio.api.models.responses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.ResponseBody;
import provisio.api.models.Guest;
import java.util.ArrayList;

/**
 * Model object for a JSON get reservations by reservation ID request response body.
 * For use in the reservation look up page, requirement 9.
 */
@Getter
@Setter
@ResponseBody
public class ReservationGetByReservationIdResponse {

    private boolean success;
    private String hotelName;
    private String roomSizeName;
    private ArrayList<Guest> guests;
    private boolean wifi;
    private boolean breakfast;
    private boolean parking;
    private String checkIn;
    private String checkOut;

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
