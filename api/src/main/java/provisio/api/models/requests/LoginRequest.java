package provisio.api.models.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import provisio.api.models.AbstractRequestResponse;

/**
 * Model object for a JSON login request body.
 * For use in the login page, requirement 6.
 */
@AllArgsConstructor
@Getter
public class LoginRequest extends AbstractRequestResponse {

    String email;
    String password;

}
