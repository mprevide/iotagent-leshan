package org.cpqd.iotagent;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.apache.log4j.Logger;
import org.eclipse.leshan.LwM2mId;
import org.eclipse.leshan.server.registration.Registration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DeviceManager {
    private Logger mLogger = Logger.getLogger(DeviceManager.class);

    private String deviceUrl;
    private Map<String, Registration> Devices = new HashMap<String, Registration>();
    private Map<String, String> Lwm2mDevices = new HashMap<String, String>();

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

    public void RegisterDevice(String service, String lwm2mId, String deviceModel, String serialNumber, Registration registration) {
        String token = TenancyManager.GetJwtToken(service);
        String query = "?attr=device_type=" + deviceModel + "&serial_number=" + serialNumber;
        String url = this.deviceUrl + query;

        try {
            HttpResponse<JsonNode> response = Unirest.get(url).header("Authorization", "Bearer " + token).asJson();
            if(response.getStatus() >= 300){
                return;
            }
            JsonNode r = response.getBody();
            String id = r.getObject().getJSONArray("devices").getJSONObject(0).get("id").toString();
            Devices.put(id, registration);
            Lwm2mDevices.put(lwm2mId, id);
            mLogger.debug(id);
        } catch (Exception e) {
            e.printStackTrace();
            mLogger.error(e);
        }
    }

    public Registration getDeviceRegistration(String id){
        return Devices.get(id);
    }
    public Registration getLwm2mRegistration(String id){
        return Devices.get(Lwm2mDevices.get(id));
    }

    public void DeregisterDevice(String lwm2mId){
        String deviceId = Lwm2mDevices.get(lwm2mId);
        Devices.remove(deviceId);
        Lwm2mDevices.remove(lwm2mId);
    }



}
