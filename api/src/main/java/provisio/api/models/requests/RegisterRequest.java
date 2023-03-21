package provisio.api.models.requests;

import lombok.Getter;

@Getter
public class RegisterRequest {

    String email;
    String password;
    String firstName;
    String lastName;

}
