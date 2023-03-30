package provisio.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * A sub-model object that is used by various reservation processes to represent guests within a list.
 */
@Getter
@Setter
@AllArgsConstructor
public class Guest {

    String firstName;
    String lastName;

}
