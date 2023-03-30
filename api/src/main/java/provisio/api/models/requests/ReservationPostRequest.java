package provisio.api.models.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import provisio.api.models.Guest;
import java.util.List;

@Getter
public class ReservationPostRequest {

    String location;
    String checkIn; //Use a string, not a date.  Java and MySQL have compatibility issues between their date objects but strings always work.
    String checkOut; //Use a string, not a date.  Java and MySQL have compatibility issues between their date objects but strings always work.
    String roomSize;
    boolean wifi;
    boolean breakfast;
    boolean parking;
    List<Guest> guests;

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
