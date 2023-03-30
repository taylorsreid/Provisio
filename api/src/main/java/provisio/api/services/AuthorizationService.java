package provisio.api.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class AuthorizationService {

    //token configuration
    private final String ISSUER = "provisio"; //issuer name

    /*
    Normally the secret would be stored in a .env file or a system variable, but since this is a school project,
    and not a production application, it is being stored in a string literal to ensure that all project members have
    access to it and that it is included in git clones.
     */
    private final Algorithm ALGORITHM = Algorithm.HMAC256("***REMOVED***");
    private final JWTVerifier VERIFIER = JWT.require(ALGORITHM).withIssuer(ISSUER).build();

    /**
     * Generates a token for the passed UUID of the user.  Does NOT perform verification or validation.
     * @param userId the UUID of the user to getReservationByReservationId a token for
     * @return a new JWT for the passed user argument
     * @throws JWTCreationException if the token can't be created
     */
    public String getTokenForUserId(String userId) throws JWTCreationException{

        //validity of token
        final int SECONDS_TO_ADD = 86400; //86400 seconds is one day

        //create and return a JWT
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(userId)
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plusSeconds(SECONDS_TO_ADD))
                .sign(ALGORITHM);
    }

    /**
     * Verifies the token contained in the authorization header.
     * @param authorizationHeader the authorization header passed.
     * @return boolean true if the token is valid, false if it is not
     */
    public boolean verifyAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader.startsWith("Bearer ")) { //check that authorization header is correctly formatted
            try { //verify that authorization header was signed with the project "secret"
                VERIFIER.verify(authorizationHeader.substring(7));
                return true;
            }
            catch (JWTVerificationException e){
                return false;
            }
        } else { //when token is invalid
            return false;
        }
    }

    /**
     * Extracts the UUID from the authorization header.
     * @param authorizationHeader the authorization header passed.
     * @return the UUID contained in the token as a string.
     */
    public String getUserIdFromAuthorizationHeader(String authorizationHeader){
        return VERIFIER.verify(authorizationHeader.substring(7)).getSubject();
    }

}
