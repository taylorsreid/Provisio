package provisio.api.models.requests;

import lombok.Getter;

@Getter
public class LoginRequest {

    String email;
    String password;

}
