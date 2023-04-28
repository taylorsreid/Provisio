package provisio.api.models.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import provisio.api.models.Guest;
import java.util.ArrayList;

/**
 * Model object for a JSON new reservation request body.
 * For use in the hotel reservation page, requirement 5.
 */
@Getter
@Setter
public class ReservationPostRequest {

    @NotBlank
    private String checkIn; //Use a string, not a date.  Java and MySQL have compatibility issues between their date objects but strings always work.
    @NotBlank
    private String checkOut; //Use a string, not a date.  Java and MySQL have compatibility issues between their date objects but strings always work.
    @NotBlank
    private String roomSizeName;
    private boolean wifi;
    private boolean breakfast;
    private boolean parking;
    private String hotelName = ""; //initialized empty because two endpoints use this model but only one uses this property
    private ArrayList<Guest> guests = new ArrayList<>(); //initialized empty because two endpoints use this model but only one uses this property

}