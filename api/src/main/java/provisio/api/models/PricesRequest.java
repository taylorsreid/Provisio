package provisio.api.models;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;

@Getter
@Setter
public class PricesRequest extends AbstractRequestResponse {

    ArrayList<String> items;

}
