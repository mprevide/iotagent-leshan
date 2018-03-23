package org.cpqd.iotagent;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.eclipse.leshan.Link;
import org.eclipse.leshan.LwM2mId;
import org.eclipse.leshan.core.model.LwM2mModel;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.model.ResourceModel;
import org.eclipse.leshan.server.registration.Registration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.File;
import java.util.*;

public class DeviceManager {

    private String deviceUrl;
    private DinamicModelProvider modelProvider;
    private Map<String, Registration> Devices = new HashMap<String, Registration>();
    private Map<String, String> Lwm2mDevices = new HashMap<String, String>();
    int newResourceId = 5000;

    // Todo(jsiloto): This would be nicer as a JsonDeserializer for generic attributes
    public static String getStaticValue(String label, JSONObject data) {
        // Get device label and new FW Version
        String value = "";
        data = data.getJSONObject("attrs");
        Iterator<?> templates = data.keys();
        while (templates.hasNext()) {
            String template = (String) templates.next();
            JSONArray attrs = data.getJSONArray(template);
            for (int i = 0; i < attrs.length(); i++) {
                JSONObject attr = (JSONObject) attrs.get(i);
                if (attr.getString("label").equals(label)) {
                    value = attr.getString("static_value");
                }
            }
        }
        return value;
    }


    // TODO(jsiloto): Isolate attribute

    // TODO(jsiloto): Check if attribute has metadata

    // TODO(jsiloto): Check if attribute has lwm2m metadata

    // TODO(jsiloto): Parse path

    // TODO(jsiloto): Check if Object exists

    // TODO(jsiloto): If doesn't exist deserialize object into resources

    // TODO(jsiloto): Update Lwm2mModel



    public void RegisterModel(JsonElement device) {


        Map<Integer, LinkedList<ResourceModel>> newModels =  new HashMap<Integer, LinkedList<ResourceModel>>();
        LinkedList<ResourceModel> resources;

        String deviceLabel = device.getAsJsonObject().get("label").getAsString();


        // Get all ResourceModels for each attribute
        JsonObject data = device.getAsJsonObject().get("attrs").getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entrySet = data.entrySet();
        for(Map.Entry<String,JsonElement> entry : entrySet){
            for(JsonElement attr: entry.getValue().getAsJsonArray()){
                System.out.println(attr);
                DeviceAttribute dev = new DeviceAttribute(attr);
                String path = DeviceAttribute.getLwm2mPath(attr);
                if(!path.isEmpty()){
                    path = StringUtils.stripStart(path, "/");
                    String[] ids = path.split("/");
                    LinkedList<ResourceModel> r = newModels.get(Integer.valueOf(ids[0]));
                    if(r == null){
                        newModels.put(Integer.valueOf(ids[0]), new LinkedList<ResourceModel>());
                    }
                    newModels.get(Integer.valueOf(ids[0])).add(dev.getLwm2mResourceModel(Integer.valueOf(ids[2])));
                }
            }
        }

        // Iterate over discovered models, add if not already in the provider
        for(Map.Entry<Integer, LinkedList<ResourceModel>> entry: newModels.entrySet()){
            int i = entry.getKey();
            LwM2mModel model = modelProvider.getObjectModel(null);
            ObjectModel oldModel = model.getObjectModel(i);
            if(oldModel == null){
                ObjectModel objectModel = new ObjectModel(newResourceId, deviceLabel,
                        "", "1", false, false, entry.getValue());
                newResourceId ++;
                modelProvider.addObjectModel(objectModel);
            }
        }






//            String template = (String) templates.next();
//            JsonArray attr_list = data.get()








//
//
//            JSONArray attr_list = data.getJSONArray(template);
//            for (int i = 0; i < attr_list.length(); i++) {
//                JSONObject attr = (JSONObject) attr_list.get(i);
//
//                // Check if attribute has Lwm2m path
//                if(attr.has("metadata")){
//
//
//
//
//                    if(attr.getJSONObject("metadata").has("path")){
//                        String path = attr.getJSONObject("metadata").getString("path");
//                        String[] ids = path.split("/");
//
//
//                        //Check if object exists in modelprovider if not create
//                        //Check if resource exists in model
//                        //update model
//                    }
//                }
//
//
//
//                String resourceName = attr.getString("label");
//                String valueType = attr.getString("value_type");
//                String type = attr.getString("type");
//                String path = "";
//
//
//
//
//                attr = (JSONObject) attr_list.get(i);
//                if (attr.getString("label").equals(resourceName)) {
//                    String value = attr.getString("static_value");
//                }
//
//                ResourceModel model = new ResourceModel(i, resourceName, ResourceModel.Operations.RW, false,
//                        false, ResourceModel.Type.STRING, "", "", "");
//
//
//            }
//        }

//        int newId = 5000;
//        ObjectModel objectModel = new ObjectModel(newId, deviceLabel, "", false, false, );


    }


    public DeviceManager(String deviceManagerUrl, DinamicModelProvider modelProvider) {
        this.deviceUrl = deviceManagerUrl + "/device";
        this.modelProvider = modelProvider;
    }

    public void RegisterDevice(String service, String lwm2mId, String deviceModel, String serialNumber, Registration registration) {
        String token = TenancyManager.GetJwtToken(service);
        String query = "?attr=device_type=" + deviceModel + "&serial_number=" + serialNumber;
        String url = this.deviceUrl + query;

        try {
            HttpResponse<JsonNode> response = Unirest.get(url).header("Authorization", "Bearer " + token).asJson();
            if (response.getStatus() >= 300) {
                return;
            }
            JsonNode r = response.getBody();
            String id = r.getObject().getJSONArray("devices").getJSONObject(0).get("id").toString();
            Devices.put(id, registration);
            Lwm2mDevices.put(lwm2mId, id);
            System.out.println(id);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    public Registration getDeviceRegistration(String id) {
        return Devices.get(id);
    }

    public Registration getLwm2mRegistration(String id) {
        return Devices.get(Lwm2mDevices.get(id));
    }

    public void DeregisterDevice(String lwm2mId) {
        String deviceId = Lwm2mDevices.get(lwm2mId);
        Devices.remove(deviceId);
        Lwm2mDevices.remove(lwm2mId);
    }


}
