package provisio.api.services;

import provisio.api.db.ConnectionManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import provisio.api.responses.RegisterResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RegisterService {

    public ResponseEntity<String> register(String email, String password, String firstName, String lastName) {

        boolean availableEmail = false;

        //check that email doesn't already exist
        try{
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT EXISTS(SELECT * FROM `users` WHERE email=?) as `exists`;");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            rs.next();
            availableEmail = !rs.getBoolean("exists"); //if email exists, it isn't available, thus the logical negation
            conn.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        //validate email is real
        //TODO: fix regex to meet project requirements
        Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPattern.matcher(email);
        boolean validEmail = matcher.find();

        //validate password meets requirements
        //TODO: fix regex to meet project requirements
        Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
        Matcher passwordMatcher = passwordPattern.matcher(password);
        boolean validPassword = passwordMatcher.find();

        //if password meets requirements and email isn't taken
        if (availableEmail && validEmail && validPassword) {

            //create encoder and encode raw password into a hashed one
            BCryptPasswordEncoder bcpe = new BCryptPasswordEncoder();
            String hashedPassword = bcpe.encode(password);

            //if hashed password is weak, keep reencoding until it isn't then set it
            while (bcpe.upgradeEncoding(hashedPassword)) {
                hashedPassword = bcpe.encode(password);
            }

            try{
                Connection conn = ConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement("INSERT INTO `users` (`customer_id`, `email`, `first_name`, `last_name`, `hashed_password`) VALUES (UUID(), ?, ?, ?, ?)");
                ps.setString(1, email);
                ps.setString(2, firstName);
                ps.setString(3, lastName);
                ps.setString(4, hashedPassword);
                ps.execute();

                //will only return when all 3 booleans are true and account has been created
                return ResponseEntity.ok(new RegisterResponse(true, true, true, true).toString());

            }
            catch (Exception ex){
                ex.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }

        }
        else {
            //set failure message and return reasons why
            return ResponseEntity.badRequest().body(new RegisterResponse(false, availableEmail, validEmail, validPassword).toString());
        }

    }

}
