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

    @Autowired
    LoginService loginService;

    public ResponseEntity<String> register(RegisterRequest registerRequest) {

        boolean availableEmail;

        //check that email doesn't already exist
        try{
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT EXISTS(SELECT * FROM `customers` WHERE email=?) as `exists`;");
            ps.setString(1, registerRequest.getEmail());
            ResultSet rs = ps.executeQuery();
            rs.next();
            availableEmail = !rs.getBoolean("exists"); //if email exists, it isn't available, thus the logical negation
            conn.close();
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
            BCryptPasswordEncoder bcpe = new BCryptPasswordEncoder();
            String hashedPassword = bcpe.encode(registerRequest.getPassword());

            //if hashed password is weak, keep reencoding until it isn't then set it
            while (bcpe.upgradeEncoding(hashedPassword)) {
                hashedPassword = bcpe.encode(registerRequest.getPassword());
            }

            try{
                Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement("INSERT INTO `customers` (`customer_id`, `email`, `first_name`, `last_name`, `hashed_password`) VALUES (UUID(), ?, ?, ?, ?)");
                ps.setString(1, registerRequest.getEmail());
                ps.setString(2, registerRequest.getFirstName());
                ps.setString(3, registerRequest.getLastName());
                ps.setString(4, hashedPassword);
                ps.execute();

                conn.close();

                System.out.println(registerRequest.getFirstName() + " " + registerRequest.getLastName() + " has created an account.");

                //returns a login response with token if the account creation was successful
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
            return ResponseEntity.ok().body(new GenericResponse(false, message.toString()).toString());
        }

    }

}
