package provisio.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import provisio.api.db.ConnectionManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import provisio.api.models.requests.LoginRequest;
import provisio.api.models.requests.RegisterRequest;
import provisio.api.models.responses.GenericResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Pattern;

@Service
public class RegisterService {

    //start login service because registering returns the same response as logging in
    @Autowired
    LoginService loginService;

    /**
     * Takes the registration request, verifies that the email isn't already taken, verifies that the email is validly
     * formatted, verifies that the password meets project requirements of 8 characters in length and includes at least
     * one uppercase and at least one lowercase letter, and verifies the first name and last name are not blank.  If all
     * the criteria are satisfied, it encodes the password as many times as necessary to be secure, then saves all the
     * information to the database.
     * @param registerRequest a JSON body containing email, password, first name, and last name.
     * @return On success, a response entity containing a success flag, a JWT, the user's first name, and the user's
     * last name. First name and last name are included because it is returning the same as logging in would, which
     * would require the frontend to save the first and last name. On failure, the success flag will be false and a JSON
     * string under "message" will be included explaining the reason for failure.
     */
    public ResponseEntity<String> register(RegisterRequest registerRequest) {

        //try to set up connection
        Connection conn;

        try{
            conn = ConnectionManager.getConnection();

            boolean availableEmail; //default false

            //check that email doesn't already exist
            try{
                PreparedStatement ps = conn.prepareStatement("SELECT EXISTS(SELECT * FROM `users` WHERE email = ?) as `exists`;");
                ps.setString(1, registerRequest.getEmail());
                ResultSet rs = ps.executeQuery();
                rs.next();
                availableEmail = !rs.getBoolean("exists"); //if email exists, it isn't available, thus the logical negation
            }
            catch (Exception ex){
                ex.printStackTrace();
                return ResponseEntity.internalServerError().body(new GenericResponse(false, "An internal server error has occurred.").toString());
            }

            //validate email is real
            Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
            boolean validEmail = emailPattern.matcher(registerRequest.getEmail()).find();

            //validate password meets requirements
            Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z]).{8,}$");
            boolean validPassword = passwordPattern.matcher(registerRequest.getPassword()).find();

            //validate first name and last name are not blank
            boolean validFirstName = !registerRequest.getFirstName().isBlank();
            boolean validLastName = !registerRequest.getLastName().isBlank();

            //if everything meets requirements
            if (availableEmail && validEmail && validPassword && validFirstName && validLastName) {

                //create encoder and encode raw password into a hashed one
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());

                //if hashed password is weak, keep re-encoding until it isn't then set it
                while (passwordEncoder.upgradeEncoding(hashedPassword)) {
                    hashedPassword = passwordEncoder.encode(registerRequest.getPassword());
                }

                //
                try{
                    PreparedStatement ps = conn.prepareStatement("INSERT INTO `users` (`user_id`, `email`, `first_name`, `last_name`, `hashed_password`) VALUES (UUID(), ?, ?, ?, ?)");
                    ps.setString(1, registerRequest.getEmail());
                    ps.setString(2, registerRequest.getFirstName());
                    ps.setString(3, registerRequest.getLastName());
                    ps.setString(4, hashedPassword);
                    ps.execute();

                    System.out.println(registerRequest.getFirstName() + " " + registerRequest.getLastName() + " has created an account.");

                    //return a login response with token if the account creation was successful
                    conn.close();
                    return loginService.login(new LoginRequest(registerRequest.getEmail(), registerRequest.getPassword()));
                }
                catch (Exception ex){
                    ex.printStackTrace();
                    return ResponseEntity.internalServerError().body(new GenericResponse(false, "An internal server error has occurred.").toString());
                }
            }
            else {
                StringBuilder message = new StringBuilder();
                if (!availableEmail){
                    message.append(" An account already already exists for that email.");
                }
                if (!validEmail){
                    message.append(" Invalid email address.");
                }
                if (!validPassword){
                    message.append(" Invalid password. Passwords must be least 8 characters in length and include at least one uppercase letter and one lowercase letter.");
                }
                if (!validFirstName){
                    message.append(" First name cannot be blank.");
                }
                if (!validLastName){
                    message.append(" Last name cannot be blank.");
                }

                //if account creation was unsuccessful, return reasons why
                conn.close();
                return ResponseEntity.ok().body(new GenericResponse(false, message.toString()).toString());
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body(new GenericResponse(false, "An internal server error has occurred.").toString());
        }
    }
}
