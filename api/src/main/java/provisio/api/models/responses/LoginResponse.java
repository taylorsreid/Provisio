package provisio.api.models.responses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Model object for a JSON login request response body.
 */
@Getter
@Setter
@AllArgsConstructor
@ResponseBody
public class LoginResponse {

    private boolean success;
    private String jwt;
    private String firstName;
    private String lastName;

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
