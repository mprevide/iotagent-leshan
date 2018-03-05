
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
import org.json.JSONObject;


public class LwM2mAgent {

    private Map<String, Registration> Devices;


    // *********** Static Methods *************** //
    private static void getDeviceFromDeviceManager(String id){
    }



    // *********** Instance Initialization *************** //
    private static Gson gson = createGson();
    private static Gson createGson(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(LwM2mNode.class, new LwM2mNodeSerializer());
        gsonBuilder.registerTypeHierarchyAdapter(LwM2mNode.class, new LwM2mNodeDeserializer());
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        Gson thiGson = gsonBuilder.create();
        return thiGson;
    }

    private static final Map<String, String> lampLwm2m = createMap();
    private static Map<String, String> createMap()
    {
        Map<String,String> lampMapping = new HashMap<String,String>();
        lampMapping.put("name", "/5000/0/0");
        lampMapping.put("voltage", "/5000/0/1");
        lampMapping.put("luminosity", "/5000/0/2");
        return lampMapping;
    }

    // ********* Methods ****************** //

    private void registerNewDevice(Registration registration, Registration previousReg,
                                   Collection<Observation> previousObsersations){
        // Request Device from device-manager
        getDeviceFromDeviceManager("asv");
        // Register listeners for dynamic data

        System.out.println("new device: " + registration.getEndpoint());
        for(int i=0; i<registration.getObjectLinks().length; i++){
            System.out.println(registration.getObjectLinks()[i]);
        }

        try {
            ReadResponse response = server.send(registration, new ReadRequest(5000,0));
            LwM2mNode object = response.getContent();
            if (response.isSuccess()) {
                System.out.println("Device: " + object);
            }else {
                System.out.println("Failed to read:" + response.getCode() + " " + response.getErrorMessage());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    // *********** Run Server *************** //
    public void updateDevice(){

    }

    public void actuate(String message){

        message = "{'data': {u'attrs': {u'luminosity': 10.6}, 'id': u'f9b1'},\n" +
                " 'event': 'configure',\n" +
                " 'meta': {'service': u'admin'}}";

        JsonNode act = new JsonNode(message);
        JSONObject data = act.getObject().getJSONObject("data");
        String id = data.getString("id");


        JsonNode data = act;




    }



    public void run(){
        try {
            LeshanServerBuilder builder = new LeshanServerBuilder();
            // add this line if you are using leshan 1.0.0-M4 because of
            // https://github.com/eclipse/leshan/issues/392
            // builder.setSecurityStore(new InMemorySecurityStore());
            LeshanServer server = builder.build();
            server.start();


            server.getRegistrationService().addListener(new RegistrationListener() {

                public void registered(Registration registration, Registration previousReg,
                                       Collection<Observation> previousObsersations) {
                    registerNewDevice(registration, previousReg, previousObsersations);
                }

                public void updated(RegistrationUpdate update, Registration updatedReg, Registration previousReg) {
                    System.out.println("device is still here: " + updatedReg.getEndpoint());
                }

                public void unregistered(Registration registration, Collection<Observation> observations, boolean expired,
                                         Registration newReg) {
                    System.out.println("device left: " + registration.getEndpoint());
                }
            });


//            ReadResponse r_response = server.send(registration, new ReadRequest(5000, 0));
//            LwM2mNode object = r_response.getContent();
//            System.out.println("Device:" + object);
//            JsonObject jo = gson.toJsonTree(object).getAsJsonObject();
//            JsonArray resources = jo.getAsJsonArray("resources");
//            JsonArray modified_resources = new JsonArray();
//            JsonObject name = resources.get(8).getAsJsonObject(); // get object with id 14 not at [14]
//            name.addProperty("value", "-1");
//            modified_resources.add(name);
//            jo.add("resources", modified_resources);
//            System.out.println("Modified Resources:" + modified_resources);
//            object = gson.fromJson(jo, LwM2mNode.class);
//
////                        ArrayList<LwM2mNode> re = new ArrayList<LwM2mNode>();
////                        re.add(new LwM2mSingleResource(object));
//
//            LwM2mSingleResource node = LwM2mSingleResource.newResource(14, Long.valueOf(-1), Type.INTEGER);
//
//
//            System.out.println("Instances:" + object);
//            WriteResponse w_response = server.send(registration, new WriteRequest(WriteRequest.Mode.REPLACE, ContentFormat.TLV, "/3/0", object));
//
//
//            r_response = server.send(registration, new ReadRequest(3, 0));
//            object = r_response.getContent();
//            System.out.println("Device:" + object);


//                      WriteResponse response = server.send(registration, new WriteRequest(5,0,1, "coap://[2001:db8::2]:5693/data/test.hex") );





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
