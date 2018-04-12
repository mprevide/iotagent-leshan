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

    public static LinkedList<DeviceAttribute> getAttributeListFromTemplate(JsonElement template){
        LinkedList<DeviceAttribute> attrList = new LinkedList<>();
        System.out.println(template);

        for (JsonElement attr : template.getAsJsonArray()) {
            System.out.println(attr);
            attrList.add(new DeviceAttribute(attr));
        }
        return attrList;
    }

    public static LinkedList<DeviceAttribute> getAttributeList(JsonElement attrs){
        LinkedList<DeviceAttribute> attrList = new LinkedList<>();
        // Get all ResourceModels for each attribute
        JsonObject data = attrs.getAsJsonObject().get("attrs").getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entrySet = data.entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {
            attrList.addAll(getAttributeListFromTemplate(entry.getValue()));
        }
        return attrList;
    }




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
        return getStaticValue(attributes, label);
    }


    public static String getStaticValue(LinkedList<DeviceAttribute> attrs, String label) {
        for (DeviceAttribute attr : attrs) {
            if (attr.label.equals(label)) {
                return attr.staticValue;
            }
        }
        return "";
    }


    public String getTemplateId(String label) {
        return getTemplateId(attributes, label);
    }


    public static String getTemplateId(LinkedList<DeviceAttribute> attrs, String label) {
        for (DeviceAttribute attr : attrs) {
            if (attr.label.equals(label)) {
                return attr.templateId;
            }
        }
        return "";
    }


}
