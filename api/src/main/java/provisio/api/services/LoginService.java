package provisio.api.services;

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

    @ResponseBody
    public ResponseEntity<String> login(LoginRequest loginRequest){

        String customerId;
        String hashedPassword;
        String firstName;
        String lastName;

        //gets the actual user so that they can be compared to the alleged user
        try{
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT `customer_id`, `hashed_password`, `first_name`, `last_name` FROM `users` WHERE email=?;");
            ps.setString(1, loginRequest.getEmail());
            ResultSet rs = ps.executeQuery();
            rs.next();
            customerId = rs.getString("customer_id");
            hashedPassword = rs.getString("hashed_password");
            firstName = rs.getString("first_name");
            lastName = rs.getString("last_name");
            conn.close();
        }
        catch (Exception ex){
            return new ResponseEntity<>(new GenericResponse(false, "Incorrect username or password!").toString(), HttpStatus.UNAUTHORIZED);
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        //compare requested password with the actual one's hash
        if (passwordEncoder.matches(loginRequest.getPassword(), hashedPassword)){

            //create AuthorizationService object
            AuthorizationService authorizationService = new AuthorizationService();

            //create token for authorized user
            String token = authorizationService.getTokenForCustomerId(customerId);

            System.out.println(firstName + " " + lastName + " has logged in.");

            //return positive response along with JWT bearer token
            return new ResponseEntity<>(new LoginResponse(true, token, customerId, loginRequest.getEmail(), firstName, lastName).toString(), HttpStatus.OK);

        }
        else {
            //return negative response with 401 code
            return new ResponseEntity<>(new GenericResponse(false, "Incorrect username or password!").toString(), HttpStatus.UNAUTHORIZED);
        }

    }

}
