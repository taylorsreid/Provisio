package provisio.api.responses;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.ResponseBody;

@NoArgsConstructor
@ResponseBody
public class GenericResponse {

    @Setter
    private boolean success;
    private final StringBuffer message = new StringBuffer();

    public GenericResponse(boolean success, String stringBody){
        this.success = success;
        message.append(stringBody);
    }

    public void appendMessage(String newMessage){
        message.append(newMessage);
    }

    @Override
    public String toString() {
        return "{\"success\":" + success + "," +
                "\"message\":\"" + message + "\"}";
    }

}
