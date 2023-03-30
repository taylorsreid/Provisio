package provisio.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import provisio.api.db.ConnectionManager;
import provisio.api.models.requests.LoginRequest;
import provisio.api.models.responses.GenericResponse;
import provisio.api.models.responses.LoginResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Service
public class LoginService {

    //start authorization service
    @Autowired
    AuthorizationService authorizationService;

    /**
     * Connects to the database and verifies the credentials sent to it in the LoginRequest object.
     * @param loginRequest A JSON string containing the users
     * @return A ResponseEntity with a JSON body that either contains a LoginResponse object or a GenericResponse with
     * a success of false and the reason why.
     */
    @ResponseBody
    public ResponseEntity<String> login(LoginRequest loginRequest){

        String userId;
        String hashedPassword;
        String userFirstName;
        String userLastName;

        final String unauthorizedMessage = "Incorrect username or password.";

        //gets the actual user so that they can be compared to the alleged user
        try{
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT `user_id`, `hashed_password`, `user_first_name`, `user_last_name` FROM `users` WHERE email = ?");
            ps.setString(1, loginRequest.getEmail());
            ResultSet rs = ps.executeQuery();

            //if there is a result, then the user exists
            if (rs.next()){
                userId = rs.getString("user_id");
                hashedPassword = rs.getString("hashed_password");
                userFirstName = rs.getString("user_first_name");
                userLastName = rs.getString("user_last_name");
            }
            //purposely cryptic response for security reasons
            else {
                return new ResponseEntity<>(new GenericResponse(false, unauthorizedMessage).toString(), HttpStatus.UNAUTHORIZED);
            }
            conn.close();
        }
        //catch any errors, purposely do not return details for security
        catch (Exception ex){
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body(new GenericResponse(false, "An internal server error has occurred.").toString());
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        //compare requested password with the actual one's hash
        if (passwordEncoder.matches(loginRequest.getPassword(), hashedPassword)){

            //create token for authorized user
            String token = authorizationService.getTokenForUserId(userId);

            //for watching the API run
            System.out.println(userFirstName + " " + userLastName + " has logged in.");

            //return positive response along with JWT bearer token
            return new ResponseEntity<>(new LoginResponse(true, token, userFirstName, userLastName).toString(), HttpStatus.OK);

        }
        //this occurs when username is found but password doesn't match, purposely cryptic response for security reasons
        else {
            //return negative response with 401 code
            return new ResponseEntity<>(new GenericResponse(false, unauthorizedMessage).toString(), HttpStatus.UNAUTHORIZED);
        }

    }

}
