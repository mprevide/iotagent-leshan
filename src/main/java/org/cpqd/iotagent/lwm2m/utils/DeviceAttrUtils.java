package org.cpqd.iotagent.lwm2m.utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class DeviceAttrUtils {

	private DeviceAttrUtils() {

	}

	private static JSONObject get(String attr, JSONObject device) {

		JSONObject attrs = device.getJSONObject("attrs");
		JSONArray templates = device.getJSONObject("attrs").names();

		for (int j = 0; j < templates.length(); ++j) {

			JSONArray templateAttrs = attrs.getJSONArray(templates.getString(j));

			for (int i = 0; i < templateAttrs.length(); ++i) {
				JSONObject templateAttr = templateAttrs.getJSONObject(i);
				if (attr.equalsIgnoreCase(templateAttr.getString("label"))) {
					return templateAttr;
				}
			}
		}

		return null;
	}

	public static String getStringAttr(String attr, String prop, JSONObject device) {
		JSONObject result = get(attr, device);
		return result != null ? result.getString(prop) : null;
	}

	public static String getStringMetaAttr(String attr, String prop, String metaAttr, JSONObject device) {
		JSONObject jsonObject = get(attr, device);

		if (jsonObject == null || !jsonObject.has("metadata")) {
			return null;
		}
		JSONArray attrMetadata = jsonObject.getJSONArray("metadata");
		for (int k = 0; k < attrMetadata.length(); ++k) {
			JSONObject md = attrMetadata.getJSONObject(k);
			if (metaAttr.equalsIgnoreCase(md.getString("label"))) {
				return md.getString(prop);
			}
		}

		return null;

	}

}
