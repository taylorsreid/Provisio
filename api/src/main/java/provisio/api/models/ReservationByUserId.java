package provisio.api.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.ResponseBody;

@Getter
@Setter
@ResponseBody
public class ReservationByUserId {

    private String reservationId;
    String location;
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
