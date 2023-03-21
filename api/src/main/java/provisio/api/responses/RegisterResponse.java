package provisio.api.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.ResponseBody;

@Getter
@Setter
@ResponseBody
@AllArgsConstructor
public class RegisterResponse {

    private boolean success;
    private boolean availableEmail;
    private boolean validEmail;
    private boolean validPassword;

    @Override
    public String toString() {
        return "{\"success\":" + success + "," +
                "\"availableEmail\":" + availableEmail + "," +
                "\"validEmail\":" + validEmail + "," +
                "\"validPassword\":" + validPassword + "," +
                "}";
    }

}