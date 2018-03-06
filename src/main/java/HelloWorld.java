

import com.auth0.jwt.*;
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.google.gson.*;
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
import java.util.*;
import java.net.HttpURLConnection;
import java.net.URL;

import org.cpqd.iotagent.LwM2mAgent;

import org.eclipse.leshan.core.node.LwM2mPath;
import org.eclipse.leshan.core.node.LwM2mSingleResource;
import org.eclipse.leshan.core.request.ContentFormat;
import org.eclipse.leshan.core.request.WriteRequest;
import org.eclipse.leshan.core.response.WriteResponse;
import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.registration.RegistrationListener;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationUpdate;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.node.LwM2mNode;
import org.eclipse.leshan.core.model.ResourceModel.Type;


import org.eclipse.leshan.server.demo.servlet.json.LwM2mNodeSerializer;
import org.eclipse.leshan.server.demo.servlet.json.LwM2mNodeDeserializer;


import static spark.Spark.*;


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

    public static String myGet (){
        System.out.println("Get");
        return "Hello World";
    }
    private static void getDeviceFromDeviceManager(String id){
    }



    public static void main(String[] args) {
        LwM2mAgent agent = new LwM2mAgent();

        get("/update", (req, res) -> agent.actuate(req.body()));
        get("/actuate", (req, res) -> "Hello World");


        agent.run();

    }




}
