package provisio.api.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import provisio.api.models.AbstractRequestResponse;
import java.math.BigDecimal;
import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PricesResponse extends AbstractRequestResponse {

    private boolean success;
    HashMap<String, BigDecimal> prices;
    BigDecimal total;

}