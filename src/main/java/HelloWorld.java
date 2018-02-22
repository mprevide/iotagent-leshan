
import com.auth0.jwt.*;
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.codec.binary.Base64;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.algorithms.Algorithm.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import java.net.HttpURLConnection;
import java.net.URL;

public class HelloWorld {

    private static String url = "http://localhost:8000/image/";

    private static HttpURLConnection con;

    private static String GetJwtToken(String service){
        String token = "";
        Integer[] group = new Integer[1];
        group[0] = 1;

        // TODO(jsiloto) Substitute mocked values with reasonable ones
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            token = JWT.create()
                    .withClaim("userid", 1)
                    .withClaim("name", "Admin (superuser)")
                    .withArrayClaim("groups", group)
                    .withIssuedAt(new Date(1517339633))
                    .withExpiresAt(new Date(1517340053))
                    .withClaim("email", "admin@noemail.com")
                    .withClaim("profile", "admin")
                    .withIssuer("eGfIBvOLxz5aQxA92lFk5OExZmBMZDDh")
                    .withClaim("service", service)
                    .withJWTId("7e3086317df2c299cef280932da856e5")
                    .withClaim("username", "admin")
                    .sign(algorithm);
        } catch (UnsupportedEncodingException exception){
            //UTF-8 encoding not supported
        } catch (JWTCreationException exception){
            //Invalid Signing configuration / Couldn't convert Claims.
        }
        return token;
    }

    public static void main(String[] args) {
        try {
            System.out.println("Hello, World!");

            String token = GetJwtToken("admin");

            HttpResponse<InputStream> jsonResponse = Unirest.get(url+"b60aa5e9-cbe6-4b51-b76c-08cf8273db07/binary")
                    .header("Authorization", "Bearer " + token)
                    .asBinary();

            System.out.println(jsonResponse.getStatus());
            System.out.println(jsonResponse.getStatusText());
            System.out.println(jsonResponse.getBody());

        } catch (Exception e) {
            // printStackTrace method
            // prints line numbers + call stack
            e.printStackTrace();

            // Prints what exception has been thrown
            System.out.println(e);
        }
    }
}
