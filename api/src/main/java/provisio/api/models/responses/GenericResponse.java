package provisio.api.models.responses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This is a generic response object that can be used to return a success status, and if the request was unsuccessful, the reason why.
 */
@Getter
@Setter
@AllArgsConstructor
@ResponseBody
public class GenericResponse {

    private boolean success;
    private String message;

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
