package org.cpqd.iotagent;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.google.gson.*;
import com.mashape.unirest.http.*;

//import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import org.eclipse.leshan.core.model.LwM2mModel;
import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.node.codec.DefaultLwM2mNodeDecoder;
import org.eclipse.leshan.core.node.codec.DefaultLwM2mNodeEncoder;
import org.eclipse.leshan.core.node.codec.LwM2mNodeDecoder;
import org.eclipse.leshan.core.request.WriteRequest;
import org.eclipse.leshan.core.response.WriteResponse;
import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.model.LwM2mModelProvider;
import org.eclipse.leshan.server.model.StaticModelProvider;
import org.eclipse.leshan.server.registration.RegistrationListener;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationUpdate;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.node.LwM2mNode;


import org.eclipse.leshan.server.demo.servlet.json.LwM2mNodeSerializer;
import org.eclipse.leshan.server.demo.servlet.json.LwM2mNodeDeserializer;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;



public class LwM2mAgent {

    private String imageManagerUrl;
    private String deviceManagerUrl;
    private ImageDownloader imageDownloader;


    LwM2mAgent(String deviceManagerUrl, String imageManagerUrl){
        this.deviceManagerUrl = deviceManagerUrl;
        this.imageManagerUrl = imageManagerUrl;
        imageDownloader = new ImageDownloader(imageManagerUrl);
    }






    private static HttpURLConnection con;

    private Map<String, Registration> Devices = new HashMap<String, Registration>();
    private final static String[] modelPaths = new String[]{"5000.xml"};
    private LeshanServer server;
    LwM2mModelProvider modelProvider;


    // *********** Static Methods *************** //
    private static void getDeviceFromDeviceManager(String id) {
    }


    // *********** Instance Initialization *************** //
    private static Gson gson = createGson();

    private static Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(LwM2mNode.class, new LwM2mNodeSerializer());
        gsonBuilder.registerTypeHierarchyAdapter(LwM2mNode.class, new LwM2mNodeDeserializer());
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        Gson thiGson = gsonBuilder.create();
        return thiGson;
    }

    private static final Map<String, Integer[]> lampLwm2m = createMap();

    private static Map<String, Integer[]> createMap() {
        Map<String, Integer[]> lampMapping = new HashMap<String, Integer[]>();
        lampMapping.put("name", new Integer[]{5000, 0, 0});
        lampMapping.put("voltage", new Integer[]{5000, 0, 1});
        lampMapping.put("luminosity", new Integer[]{5000, 0, 2});
        lampMapping.put("firmware", new Integer[]{5, 0, 1});
        return lampMapping;
    }

    // ********* Methods ****************** //

    private void registerNewDevice(Registration registration, Registration previousReg,
                                   Collection<Observation> previousObsersations) {

        System.out.println(registration.getId());
        //Devices.put(registration.getId(), registration);
        Devices.put("f9b1", registration);
        // Request Device from device-manager
        getDeviceFromDeviceManager("asv");
        // Register listeners for dynamic data


        System.out.println("new device: " + registration.getEndpoint());
        for (int i = 0; i < registration.getObjectLinks().length; i++) {
            System.out.println(registration.getObjectLinks()[i]);
        }

        try {
            ReadResponse response = server.send(registration, new ReadRequest(5000));
            LwM2mNode object = response.getContent();
            JsonObject jo = gson.toJsonTree(object).getAsJsonObject();
            if (response.isSuccess()) {
                System.out.println("Device: " + response.getContent());
                System.out.println("Device: " + jo);
            } else {
                System.out.println("Failed to read:" + response.getCode() + " " + response.getErrorMessage());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    // *********** Run Server *************** //
    public String update(String message) {
        message = "{'data': {'attrs': {'53': [{'created': '2018-03-01T18:39:52.589519+00:00',\n" +
                "                          'id': 161,\n" +
                "                          'label': 'fw_version',\n" +
                "                          'static_value': '1.0.1',\n" +
                "                          'template_id': '53',\n" +
                "                          'type': 'static',\n" +
                "                          'value_type': 'string'},\n" +
                "                         {'created': '2018-03-01T18:39:52.590163+00:00',\n" +
                "                          'id': 162,\n" +
                "                          'label': 'voltage',\n" +
                "                          'template_id': '53',\n" +
                "                          'type': 'dynamic',\n" +
                "                          'value_type': 'float'},\n" +
                "                         {'created': '2018-03-01T18:39:52.590761+00:00',\n" +
                "                          'id': 163,\n" +
                "                          'label': 'luminosity',\n" +
                "                          'template_id': '53',\n" +
                "                          'type': 'actuator',\n" +
                "                          'value_type': 'float'},\n" +
                "                         {'created': '2018-03-01T18:39:52.591380+00:00',\n" +
                "                          'id': 164,\n" +
                "                          'label': 'name',\n" +
                "                          'static_value': 'test_template Rev01',\n" +
                "                          'template_id': '53',\n" +
                "                          'type': 'static',\n" +
                "                          'value_type': 'string'}]},\n" +
                "          'id': 'f9b1',\n" +
                "          'label': 'device',\n" +
                "          'templates': [53]},\n" +
                " 'event': 'update',\n" +
                " 'meta': {'service': 'admin'}}\n";


        JsonNode upd = new JsonNode(message);
        JSONObject data = upd.getObject().getJSONObject("data");
        String id = data.getString("id");
        Registration registration = Devices.get(id);



        data = data.getJSONObject("attrs");
        Iterator<?> templates = data.keys();
        while (templates.hasNext()) {
            try {
                String template = (String) templates.next();
                JSONArray attrs = data.getJSONArray(template);
                for (int i = 0; i < attrs.length(); i++) {
                    String newFwVersion = "";
                    String deviceLabel = "";


                    JSONObject attr = (JSONObject) attrs.get(i);
                    if (attr.getString("label").equals("fw_version")){
                        newFwVersion = attr.getString("static_value");
                    }
                    if (attr.getString("label").equals("Model Number")){
                        deviceLabel = attr.getString("static_value");
                    }

                    String currentFwVersion = "1.0.0";
                    if (!currentFwVersion.equals(newFwVersion)) {
                        imageDownloader.FetchImage("admin", deviceLabel, newFwVersion);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e);
            }

        }


        return "OK\n";
    }

    public String actuate(String message) {

        message = "{'data': {'attrs': {'luminosity': 10.6}, 'id': 'f9b1'},\n" +
                " 'event': 'configure',\n" +
                " 'meta': {'service': 'admin'}}";

        JsonNode act = new JsonNode(message);
        JSONObject data = act.getObject().getJSONObject("data");
        String id = data.getString("id");
        Registration registration = Devices.get(id);

        LwM2mModel model = modelProvider.getObjectModel(registration);
        Collection<ObjectModel> models = model.getObjectModels();


        data = data.getJSONObject("attrs");
        Iterator<?> keys = data.keys();
        while (keys.hasNext()) {
            try {
                String key = (String) keys.next();
                Integer[] path = lampLwm2m.get(key);
                Object val = data.get(key);
                if (val instanceof String) {
                    WriteResponse response = server.send(registration, new WriteRequest(path[0], path[1], path[2], (String) val));
                } else if (val instanceof Double) {
                    WriteResponse response = server.send(registration, new WriteRequest(path[0], path[1], path[2], (Double) val));
                } else if (val instanceof Boolean) {
                    WriteResponse response = server.send(registration, new WriteRequest(path[0], path[1], path[2], (Boolean) val));
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e);
            }

        }

        return "OK\n";

    }


    RegistrationListener listener = new RegistrationListener() {
        public void registered(Registration registration, Registration previousReg,
                               Collection<Observation> previousObsersations) {
            registerNewDevice(registration, previousReg, previousObsersations);
        }

        public void updated(RegistrationUpdate update, Registration updatedReg, Registration previousReg) {
        }

        public void unregistered(Registration registration, Collection<Observation> observations, boolean expired,
                                 Registration newReg) {
            System.out.println("device left: " + registration.getEndpoint());
        }
    };


    public void run() {
        try {
            LeshanServerBuilder builder = new LeshanServerBuilder();

            // Set encoder/decoders
            builder.setEncoder(new DefaultLwM2mNodeEncoder());
            LwM2mNodeDecoder decoder = new DefaultLwM2mNodeDecoder();
            builder.setDecoder(decoder);

            // Define model provider
            List<ObjectModel> models = ObjectLoader.loadDefault();
            models.addAll(ObjectLoader.loadDdfResources("/models/", modelPaths));
            modelProvider = new StaticModelProvider(models);
            builder.setObjectModelProvider(modelProvider);


            // Start Server
            server = builder.build();
            server.start();

            // Add Registration Treatment
            server.getRegistrationService().addListener(listener);


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
