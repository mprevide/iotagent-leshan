package org.cpqd.iotagent;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.leshan.core.model.ResourceModel;
import java.util.Arrays;

/*
  This class represents a generic attribute.
  It should be used as intermediary object when converting between device-manager and Lwm2m models
 */
public class DeviceAttribute {
    String label;
    String type;
    String valueType;
    String staticValue;
    String path;

    public DeviceAttribute(JsonElement json) {
        // Regular Attributes
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

        // Metadata
        path = getLwm2mPath(json);
    }

    public boolean isLwm2mAttr() {
        return (!path.isEmpty());
    }


    public Integer[] getLwm2mPath() {
        if (path.isEmpty()) {
            return null;
        }
        return getIdsfromPath(path);
    }

    public static Integer[] getIdsfromPath(String path) {
        String[] p = StringUtils.stripStart(path, "/").split("/");
        Integer[] result = Arrays.stream(p).map(s -> Integer.valueOf(s)).toArray(Integer[]::new);
        return result;
    }


    public ResourceModel getLwm2mResourceModel() {
        Integer[] ids = getLwm2mPath();
        if (ids == null) {
            return null;
        }
        return getLwm2mResourceModel(ids[2]);
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

