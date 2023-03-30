package provisio.api.models.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

/**
 * Model object for a JSON get reservations by reservation ID request body.
 * For use in the reservation look up page, requirement 9.
 */
@Getter
public class ReservationGetByReservationIdRequest {

    String reservationId;

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
