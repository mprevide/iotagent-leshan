package org.cpqd.iotagent;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONObject;

public class DeviceManager {

    private String deviceUrl;

    public DeviceManager(String deviceManagerUrl) {
        this.deviceUrl = deviceManagerUrl + "/device";
    }

    public void RegisterDevice(String service, String Lwm2mId, String DeviceModel, String SerialNumber) {
        String token = TenancyManager.GetJwtToken(service);
        String query = "?attr=device_type=" + DeviceModel + "&serial_number=" + SerialNumber;
        String url = this.deviceUrl + query;

        try {
            HttpResponse<JsonNode> response = Unirest.get(url).header("Authorization", "Bearer " + token).asJson();
            JsonNode r = response.getBody();
            String id = r.getObject().getJSONArray("devices").getJSONObject(0).get("id").toString();
            System.out.println(id);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }


    }
}
