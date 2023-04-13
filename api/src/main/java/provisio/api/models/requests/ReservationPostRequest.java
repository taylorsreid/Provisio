package provisio.api.models.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import provisio.api.models.Guest;
import java.util.ArrayList;

/**
 * Model object for a JSON new reservation request body.
 * For use in the hotel reservation page, requirement 5.
 */
@Getter
@Setter
public class ReservationPostRequest {

    private String hotelName;
    private String checkIn; //Use a string, not a date.  Java and MySQL have compatibility issues between their date objects but strings always work.
    private String checkOut; //Use a string, not a date.  Java and MySQL have compatibility issues between their date objects but strings always work.
    private String roomSizeName;
    private boolean wifi;
    private boolean breakfast;
    private boolean parking;
    private ArrayList<Guest> guests;

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}