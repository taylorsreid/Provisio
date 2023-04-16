package provisio.api.models.requests;

import lombok.Getter;
import provisio.api.models.AbstractRequestResponse;

/**
 * Model object for a JSON registration request body.
 * For use in the registration page, requirement 5.
 */
@Getter
public class RegisterRequest extends AbstractRequestResponse {

    String email;
    String password;
    String firstName;
    String lastName;

}
