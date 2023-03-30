package provisio.api.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * A sub-model object that is used by the customer loyalty page that is used to represent reservations within a list.
 */
@Getter
@Setter
@ResponseBody
public class IndividualReservation {

    private String reservationId;
    String hotel;
    String checkIn;
    String checkOut;
    int pointsEarned;

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
