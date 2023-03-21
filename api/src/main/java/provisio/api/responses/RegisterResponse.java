package provisio.api.responses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.ResponseBody;

@Getter
@Setter
@AllArgsConstructor
@ResponseBody
public class RegisterResponse {

    private boolean success;
    private boolean availableEmail;
    private boolean validEmail;
    private boolean validPassword;

    @Override
    public String toString() {
//        return "{\"success\":" + success + "," +
//                "\"availableEmail\":" + availableEmail + "," +
//                "\"validEmail\":" + validEmail + "," +
//                "\"validPassword\":" + validPassword + "," +
//                "}";

        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}