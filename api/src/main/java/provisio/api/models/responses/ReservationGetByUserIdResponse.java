package provisio.api.models.responses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.ResponseBody;
import provisio.api.models.ReservationByUserId;

import java.util.ArrayList;

@Getter
@Setter
@ResponseBody
public class ReservationGetByUserIdResponse {

    private boolean success;
    ArrayList<ReservationByUserId> reservations;
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
