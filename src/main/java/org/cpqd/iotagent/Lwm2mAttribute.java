package org.cpqd.iotagent;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Iterator;

public class Lwm2mAttribute{

    public static String getPath(JsonElement json){
        JsonObject obj = json.getAsJsonObject().get("metadata").getAsJsonObject();
        for (int i = 0; i < obj.getAsJsonArray().size(); i++) {
            DeviceAttribute attr = new DeviceAttribute(obj.getAsJsonArray().get(i));
            if(attr.type.equals("lwm2m")){
                return attr.staticValue;
            }
        }
        return "";
    }

    public static boolean isLwm2mMetadata(JsonElement json){
        JsonObject type = json.getAsJsonObject().get("type").getAsJsonObject();
        if(type != null){
            if(type.getAsString().equals("lwm2m")){
                return true;
            }
        }
        return false;
    }



}
