package provisio.api.models.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import provisio.api.models.Guest;
import java.util.ArrayList;

/**
 * Model object for a JSON new reservation request body.
 * For use in the hotel reservation page, requirement 5.
 */
@Getter
public class ReservationPostRequest {

    String hotel;
    String checkIn; //Use a string, not a date.  Java and MySQL have compatibility issues between their date objects but strings always work.
    String checkOut; //Use a string, not a date.  Java and MySQL have compatibility issues between their date objects but strings always work.
    String roomSize;
    boolean wifi;
    boolean breakfast;
    boolean parking;
    ArrayList<Guest> guests;

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
