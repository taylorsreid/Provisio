package provisio.api.models.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

/**
 * Model object for a JSON registration request body.
 * For use in the registration page, requirement 5.
 */
@Getter
public class RegisterRequest {

    String email;
    String password;
    String firstName;
    String lastName;

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
