package provisio.api.models.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

@Getter
public class ReservationPostRequest {

    int hotelId;
    String checkIn;
    String checkOut;
    int roomSizeId;
    boolean wifi;
    boolean breakfast;
    boolean parking;
    int guests;

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
