package org.cpqd.iotagent;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class Device {

    String label;
    String deviceId;
    public LinkedList<DeviceAttribute> attributes;

    public Device(JsonElement device) {
        label = device.getAsJsonObject().get("label").getAsString();
        deviceId = device.getAsJsonObject().get("id").getAsString();
        attributes = new LinkedList<>();
        // Get all ResourceModels for each attribute
        JsonObject data = device.getAsJsonObject().get("attrs").getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entrySet = data.entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {
            for (JsonElement attr : entry.getValue().getAsJsonArray()) {
                System.out.println(attr);
                attributes.add(new DeviceAttribute(attr));
            }
        }
    }

    public String getStaticValue(String label) {
        for (DeviceAttribute attr : attributes) {
            if (attr.label.equals(label)) {
                return attr.staticValue;
            }
        }
        return "";
    }


}
