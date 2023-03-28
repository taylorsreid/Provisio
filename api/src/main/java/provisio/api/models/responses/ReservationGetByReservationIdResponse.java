package provisio.api.models.responses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.ResponseBody;
import provisio.api.models.Guest;

import java.util.ArrayList;

@Getter
@Setter
@ResponseBody
public class ReservationGetByReservationIdResponse {

    private boolean success;
    private String locationName;
    private String roomSizeName;
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
