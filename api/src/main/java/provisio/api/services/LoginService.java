package provisio.api.services;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import provisio.api.db.ConnectionManager;
import provisio.api.responses.GenericResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Service
public class LoginService {

    @ResponseBody
    public ResponseEntity<String> login(String email, String allegedPassword){

        String customerId;
        String hashedPassword;

        //gets the actual user so that they can be compared to the alleged user
        try{
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT `customer_id`, `hashed_password` FROM `users` WHERE email=?;");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            rs.next();
            customerId = rs.getString("customer_id");
            hashedPassword = rs.getString("hashed_password");
            conn.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
            return new ResponseEntity<>(new GenericResponse(false, "Incorrect username or password!").toString(), HttpStatus.UNAUTHORIZED);
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        //compare requested password with the actual one's hash
        if (passwordEncoder.matches(allegedPassword, hashedPassword)){

            //create AuthorizationService object
            AuthorizationService authorizationService = new AuthorizationService();

            //create token for authorized user
            String token = authorizationService.getTokenForUserId(customerId);

            //create headers to add to bearer token to
            HttpHeaders httpHeaders = new HttpHeaders();

            //add token to headers
            httpHeaders.setBearerAuth(token);

            //return positive response along with JWT bearer token
            return new ResponseEntity<>(new GenericResponse(true, "Successfully logged in.").toString(), httpHeaders, 200);

        }
        else {
            //return negative response with 401 code
            return new ResponseEntity<>(new GenericResponse(false, "Incorrect username or password!").toString(), HttpStatus.UNAUTHORIZED);
        }

    }

}
