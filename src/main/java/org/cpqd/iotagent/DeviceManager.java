package org.cpqd.iotagent;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.eclipse.leshan.server.registration.Registration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DeviceManager {

    private String deviceUrl;
    private Map<String, Registration> Devices = new HashMap<String, Registration>();

    public static String getStaticValue(String label, JSONObject data ){
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



    public DeviceManager(String deviceManagerUrl) {
        this.deviceUrl = deviceManagerUrl + "/device";
    }

    public void RegisterDevice(String service, String Lwm2mId, String DeviceModel, String SerialNumber, Registration registration) {
        String token = TenancyManager.GetJwtToken(service);
        String query = "?attr=device_type=" + DeviceModel + "&serial_number=" + SerialNumber;
        String url = this.deviceUrl + query;

        try {
            HttpResponse<JsonNode> response = Unirest.get(url).header("Authorization", "Bearer " + token).asJson();
            JsonNode r = response.getBody();
            String id = r.getObject().getJSONArray("devices").getJSONObject(0).get("id").toString();
            Devices.put(id, registration);
            System.out.println(id);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    public Registration getRegistration(String id){
        return Devices.get(id);
    }




}
