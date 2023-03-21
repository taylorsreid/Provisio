package provisio.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.net.URI;

@Service
public class ReservationService {

    @Autowired
    private AuthorizationService authorizationService;

    public ResponseEntity<String> post(String authorizationHeader){
        if(authorizationHeader != null && authorizationService.verifyAuthorizationHeader(authorizationHeader)){
            return null;
        }
        else{
            return redirectToLogin();
        }
    }

    public ResponseEntity<String> get(String authorizationHeader){
        if(authorizationHeader != null && authorizationService.verifyAuthorizationHeader(authorizationHeader)){
            return null;
        }
        else{
            return redirectToLogin();
        }
    }

    private ResponseEntity<String> redirectToLogin(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("https://taylorsreid.github.io/Provisio/login.html"));
        return new ResponseEntity<>("Redirecting you to the login page...", httpHeaders, 302);
    }

}
