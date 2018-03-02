
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


public class HelloWorld {

    private static String url = "http://localhost:8000/image/";

    private static final Map<String, String> Device2Lwm2m = createMap();
    private static Map<String, String> createMap()
    {
        Map<String,String> myMap = new HashMap<String,String>();
        myMap.put("a", "b");
        myMap.put("c", "d");
        return myMap;
    }


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


            LeshanServerBuilder builder = new LeshanServerBuilder();
            // add this line if you are using leshan 1.0.0-M4 because of
            // https://github.com/eclipse/leshan/issues/392
            // builder.setSecurityStore(new InMemorySecurityStore());
            LeshanServer server = builder.build();
            server.start();

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeHierarchyAdapter(LwM2mNode.class, new LwM2mNodeSerializer());
            gsonBuilder.registerTypeHierarchyAdapter(LwM2mNode.class, new LwM2mNodeDeserializer());
            gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
            Gson gson = gsonBuilder.create();



            server.getRegistrationService().addListener(new RegistrationListener() {

                public void registered(Registration registration, Registration previousReg,
                                       Collection<Observation> previousObsersations) {


                    System.out.println("new device: " + registration.getEndpoint());

                    for(int i=0; i<registration.getObjectLinks().length; i++){
                        System.out.println(registration.getObjectLinks()[i]);
                    }

//                    System.out.println("links: " + );
                    try {
                        ReadResponse r_response = server.send(registration, new ReadRequest(3, 0));
                        LwM2mNode object = r_response.getContent();
                        System.out.println("Device:" + object);
                        JsonObject jo = gson.toJsonTree(object).getAsJsonObject();
                        JsonArray resources = jo.getAsJsonArray("resources");
                        JsonArray modified_resources = new JsonArray();
                        JsonObject name = resources.get(8).getAsJsonObject(); // get object with id 14 not at [14]
                        name.addProperty("value", "-1");
                        modified_resources.add(name);
                        jo.add("resources", modified_resources);
                        System.out.println("Modified Resources:" + modified_resources);
                        object = gson.fromJson(jo, LwM2mNode.class);

//                        ArrayList<LwM2mNode> re = new ArrayList<LwM2mNode>();
//                        re.add(new LwM2mSingleResource(object));

                        LwM2mSingleResource node = LwM2mSingleResource.newResource(14, Long.valueOf(-1), Type.INTEGER);


                        System.out.println("Instances:" + object);
                        WriteResponse w_response = server.send(registration, new WriteRequest(WriteRequest.Mode.REPLACE, ContentFormat.TLV, "/3/0", object));


                        r_response = server.send(registration, new ReadRequest(3, 0));
                        object = r_response.getContent();
                        System.out.println("Device:" + object);


//                      WriteResponse response = server.send(registration, new WriteRequest(5,0,1, "coap://[2001:db8::2]:5693/data/test.hex") );
                        ReadResponse response = server.send(registration, new ReadRequest(3,0,13));
                        if (response.isSuccess()) {
                            System.out.println("Device time:" + ((LwM2mResource)response.getContent()).getValue());
                        }else {
                            System.out.println("Failed to read:" + response.getCode() + " " + response.getErrorMessage());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                public void updated(RegistrationUpdate update, Registration updatedReg, Registration previousReg) {
                    System.out.println("device is still here: " + updatedReg.getEndpoint());
                }

                public void unregistered(Registration registration, Collection<Observation> observations, boolean expired,
                                         Registration newReg) {
                    System.out.println("device left: " + registration.getEndpoint());
                }
            });









//            System.out.println("Hello, World!");
//
//            String token = GetJwtToken("admin");
//
//            HttpResponse<InputStream> jsonResponse = Unirest.get(url+"b60aa5e9-cbe6-4b51-b76c-08cf8273db07/binary")
//                    .header("Authorization", "Bearer " + token)
//                    .asBinary();
//
//            System.out.println(jsonResponse.getStatus());
//            System.out.println(jsonResponse.getStatusText());
//            System.out.println(jsonResponse.getBody());

        } catch (Exception e) {
            // printStackTrace method
            // prints line numbers + call stack
            e.printStackTrace();

            // Prints what exception has been thrown
            System.out.println(e);
        }
    }




}
