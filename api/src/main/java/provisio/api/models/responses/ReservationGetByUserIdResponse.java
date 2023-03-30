package provisio.api.models.responses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.ResponseBody;
import provisio.api.models.IndividualReservation;

import java.util.ArrayList;

/**
 * Model object for a JSON get reservations by user ID request response body.
 * For use in the customer loyalty points tracking page, requirement 10.
 */
@Getter
@Setter
@ResponseBody
public class ReservationGetByUserIdResponse {

    private boolean success;
    ArrayList<IndividualReservation> reservations;
    int totalPointsEarned;

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
