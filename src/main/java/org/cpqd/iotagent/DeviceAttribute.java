package org.cpqd.iotagent;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.eclipse.leshan.core.model.ResourceModel;

import java.util.LinkedList;
import java.util.List;

public class DeviceAttribute {
    String label;
    String type;
    String valueType;
    String staticValue;

    public DeviceAttribute(JsonElement json) {
        label = json.getAsJsonObject().get("label").getAsString();
        type = json.getAsJsonObject().get("type").getAsString();
        valueType = json.getAsJsonObject().get("value_type").getAsString();
        if (type.equals("dynamic")) {
            // dynamic does not have a value
        } else if (type.equals("actuator")) {
            // Actuator does not have a value
        } else {
            staticValue = json.getAsJsonObject().get("static_value").getAsString();
        }
    }

    public ResourceModel getLwm2mResourceModel(int num) {
        ResourceModel model = new ResourceModel(num, label, getOpsFor(type), false, false,
                getTypeFor(valueType), "", "", "");
        return model;
    }


    public static String getLwm2mPath(JsonElement json) {
        if (!json.getAsJsonObject().has("metadata")) {
            return "";
        }
        JsonArray obj = json.getAsJsonObject().get("metadata").getAsJsonArray();
        for (int i = 0; i < obj.size(); i++) {
            DeviceAttribute attr = new DeviceAttribute(obj.get(i));
            if (attr.type.equals("lwm2m")) {
                return attr.staticValue;
            }
        }
        return "";
    }

    private ResourceModel.Type getTypeFor(String valueType) {
        switch (valueType) {
            case "bool":
            case "boolean":
                return ResourceModel.Type.BOOLEAN;
            case "string":
                return ResourceModel.Type.STRING;
            case "float":
                return ResourceModel.Type.FLOAT;
            case "integer":
                return ResourceModel.Type.INTEGER;
            case "geo":
                return ResourceModel.Type.STRING;
            default:
                throw new IllegalArgumentException("Invalid value_type " + valueType);
        }
    }

    private ResourceModel.Operations getOpsFor(String type) {
        switch (type) {
            case "actuator":
                return ResourceModel.Operations.RW;
            case "dynamic":
                return ResourceModel.Operations.R;
            case "static":
                return ResourceModel.Operations.R;
            case "meta":
                return ResourceModel.Operations.NONE;
            default:
                throw new IllegalArgumentException("Invalid type " + type);
        }
    }


}

