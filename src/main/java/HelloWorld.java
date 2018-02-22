
import com.auth0.jwt.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.codec.binary.Base64;
import com.auth0.jwt.algorithms.Algorithm;
import java.io.BufferedReader;
import java.io.InputStreamReader;
//import java.io.DataOutputStream;
import java.util.Map;
import java.util.HashMap;
import java.net.HttpURLConnection;
import java.net.URL;

public class HelloWorld {

    private static HttpURLConnection con;

    public static void main(String[] args) {
        try {
            System.out.println("Hello, World!");
            URL url = new URL("http://localhost:8000/image/");

            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");


            Map<String, String> parameters = new HashMap<>();
            parameters.put("param1", "val");

            DecodedJWT jwt = JWT.require(Algorithm.HMAC256("secret"));


            con.setRequestProperty("Content-Type", "application/json");
            String contentType = con.getHeaderField("Content-Type");
            System.out.println(contentType);


//            con.setConnectTimeout(5000);
//            con.setReadTimeout(5000);


//            con.setDoOutput(true);
//            DataOutputStream out = new DataOutputStream(con.getOutputStream());
//            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
//            out.flush();
//            out.close();




            int status = con.getResponseCode();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            con.disconnect();


        } catch (Exception e) {
            con.disconnect();
            // printStackTrace method
            // prints line numbers + call stack
            e.printStackTrace();

            // Prints what exception has been thrown
            System.out.println(e);
        }
    }
}
