package org.cpqd.iotagent;


import com.eclipsesource.json.Json;
import com.google.gson.JsonElement;
import com.google.gson.*;
import com.mashape.unirest.http.*;

import java.net.HttpURLConnection;
import java.util.*;

import org.apache.http.annotation.Obsolete;
import org.cpqd.iotagent.kafka.KafkaHandler;
import org.eclipse.leshan.core.model.LwM2mModel;
import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.node.codec.DefaultLwM2mNodeDecoder;
import org.eclipse.leshan.core.node.codec.DefaultLwM2mNodeEncoder;
import org.eclipse.leshan.core.node.codec.LwM2mNodeDecoder;
import org.eclipse.leshan.core.request.ObserveRequest;
import org.eclipse.leshan.core.request.WriteRequest;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.model.LwM2mModelProvider;
import org.eclipse.leshan.server.model.StaticModelProvider;
import org.eclipse.leshan.server.observation.ObservationListener;
import org.eclipse.leshan.server.registration.RegistrationListener;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationUpdate;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.server.observation.ObservationListener;
import org.eclipse.leshan.core.node.LwM2mNode;


import org.json.JSONObject;


public class LwM2mAgent implements Runnable{

    private String imageManagerUrl;
    private String deviceManagerUrl;
    private ImageDownloader imageDownloader;
    private DeviceManager deviceManager;
    private LwM2mHandler requestHandler;
    private Gson gson;
    private LeshanServer server;
    private LwM2mModelProvider modelProvider;

    private static HttpURLConnection con;
    private final static String[] modelPaths = new String[]{"5000.xml"};
    private KafkaHandler kafkaHandler;

    LwM2mAgent(KafkaHandler kafkaHandler, String deviceManagerUrl, String imageManagerUrl)  {
        this.deviceManagerUrl = deviceManagerUrl;
        this.imageManagerUrl = imageManagerUrl;
        this.gson = gson = createGson();
        this.kafkaHandler = kafkaHandler;


        // Define model provider
        List<ObjectModel> models = ObjectLoader.loadDefault();
        models.addAll(ObjectLoader.loadDdfResources("/models/", modelPaths));
        DinamicModelProvider dynamDinamicModelProvider = new DinamicModelProvider(models);

        modelProvider = dynamDinamicModelProvider;
        imageDownloader = new ImageDownloader(imageManagerUrl);
        deviceManager = new DeviceManager(deviceManagerUrl, dynamDinamicModelProvider);
    }

    // *********** Instance Initialization *************** //
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

    private void registerNewDevice(Registration registration) {

        //Get ID

        // Check device manager if device exists, if not drop
        String DeviceModel = requestHandler.ReadResource(registration, 3, 0, 1);
        String SerialNumber = requestHandler.ReadResource(registration, 3, 0, 2);
        System.out.println(DeviceModel + " / " + SerialNumber);
        String Lwm2mId = registration.getId();
        JsonElement deviceJson = deviceManager.GetDeviceFromDeviceManager("admin", DeviceModel, SerialNumber);
        Device device = new Device(deviceJson);
        deviceManager.RegisterDevice(device, "admin", Lwm2mId, DeviceModel, SerialNumber, registration);


        // TODO(jsiloto) Register listeners for dynamic data
        for(DeviceAttribute attr: device.attributes){
            if(attr.type.equals("dynamic")){
                Integer[] path = attr.getLwm2mPath();
                requestHandler.ObserveResource(registration, path[0], path[1], path[2]);
            }
        }

        // TODO(jsiloto): Is anything bellow this line useful?
        System.out.println("new device: " + registration.getEndpoint());
        for (int i = 0; i < registration.getObjectLinks().length; i++) {
            System.out.println(registration.getObjectLinks()[i]);
        }
    }


    // *********** Run Server *************** //
    public String create(String message) {
        JsonElement o = new JsonParser().parse(message);
        Device device = new Device(o);
        deviceManager.RegisterModel(device);
        return "OK\n";
    }


    public String update(String message) {
        JsonElement o = new JsonParser().parse(message);
        Device device = new Device(o);
        deviceManager.RegisterModel(device);

        // Retrieve device id
        String id = device.deviceId;
        Registration registration = deviceManager.getDeviceRegistration(id);
        if (registration == null) {
            return "NOK\n";
        }


        System.out.println(registration);


        // Get device label and new FW Version
        String newFwVersion = device.getStaticValue("fw_version");
        String deviceLabel = device.getStaticValue("device_type");

        // Get device current FW version
        String currentFwVersion = requestHandler.ReadResource(registration, 3, 0, 3);

        // If Version has changed Update
        if (!currentFwVersion.equals(newFwVersion)) {
            String imageID = imageDownloader.FetchImage("admin", deviceLabel, newFwVersion);
            String fileserverUrl = "coap://[2001:db8::2]:5693/data/";
            String fileUrl = fileserverUrl + imageID + ".hex";
            requestHandler.WriteResource(registration, 5, 0, 1, fileUrl);
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
        Registration registration = deviceManager.getDeviceRegistration(id);

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


    private final RegistrationListener registrationListener = new RegistrationListener() {
        public void registered(Registration registration, Registration previousReg,
                               Collection<Observation> previousObsersations) {
            registerNewDevice(registration);
        }

        public void updated(RegistrationUpdate update, Registration updatedReg, Registration previousReg) {
            if (deviceManager.getLwm2mRegistration(updatedReg.getId()) == null) {
                registerNewDevice(updatedReg);
            }
        }

        public void unregistered(Registration registration, Collection<Observation> observations, boolean expired,
                                 Registration newReg) {
            System.out.println("device left: " + registration.getEndpoint());
            deviceManager.DeregisterDevice(registration.getId());
        }
    };

    private final ObservationListener observationListener = new ObservationListener() {
        @Override
        public void cancelled(Observation observation) {
        }

        @Override
        public void onResponse(Observation observation, Registration registration, ObserveResponse response) {
            JsonElement element = gson.toJsonTree(response.getContent());
            System.out.println("Received notification from [" + observation.getPath() + "] containing value:" + element);
            JsonObject attrs = new JsonObject();
            String label = deviceManager.getLabelFromPath(observation.getPath().toString());
            attrs.add(label, element.getAsJsonObject().get("value"));
            String deviceId = deviceManager.getDeviceId(observation.getRegistrationId());
            kafkaHandler.UpdateAttr("admin", deviceId, attrs);
        }

        @Override
        public void onError(Observation observation, Registration registration, Exception error) {
            System.out.println("Unable to handle notification of" + observation.getRegistrationId().toString() + ": " + observation.getPath());
        }

        @Override
        public void newObservation(Observation observation, Registration registration) {
        }
    };




    @Override
    public void run() {
        try {
            LeshanServerBuilder builder = new LeshanServerBuilder();

            // Set encoder/decoders
            builder.setEncoder(new DefaultLwM2mNodeEncoder());
            LwM2mNodeDecoder decoder = new DefaultLwM2mNodeDecoder();
            builder.setDecoder(decoder);

            // Define model provider
            builder.setObjectModelProvider(modelProvider);


            // Start Server
            server = builder.build();
            server.start();

            // Add Registration Treatment
            server.getRegistrationService().addListener(registrationListener);
            server.getObservationService().addListener(observationListener);

            // Initialize Request Handler
            requestHandler = new LwM2mHandler(server, gson);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }


}
