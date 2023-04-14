package provisio.api.models.requests;

import lombok.Getter;
import provisio.api.models.AbstractRequestResponse;

/**
 * Model object for a JSON get reservations by reservation ID request body.
 * For use in the reservation look up page, requirement 9.
 */
@Getter
public class ReservationGetByReservationIdRequest extends AbstractRequestResponse {

    String reservationId;

}
