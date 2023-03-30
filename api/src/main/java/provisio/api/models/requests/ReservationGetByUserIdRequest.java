package provisio.api.models.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

/**
 * Model object for a JSON get reservations by user ID request body.
 * For use in the customer loyalty points tracking page, requirement 10.
 */
@Getter
public class ReservationGetByUserIdRequest {

    String userId;

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
