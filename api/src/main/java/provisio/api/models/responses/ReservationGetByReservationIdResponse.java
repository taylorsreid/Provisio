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
    private String hotel;
    private String roomSize;
    ArrayList<Guest> guests;
    boolean wifi;
    boolean breakfast;
    boolean parking;
    String checkIn;
    String checkOut;

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
