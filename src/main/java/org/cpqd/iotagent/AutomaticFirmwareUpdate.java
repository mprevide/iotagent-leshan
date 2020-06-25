package org.cpqd.iotagent;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.cpqd.iotagent.lwm2m.utils.DeviceAttrUtils;
import org.eclipse.leshan.server.registration.Registration;
import org.json.JSONObject;

public class AutomaticFirmwareUpdate {

	private Logger logger = Logger.getLogger(AutomaticFirmwareUpdate.class);

	public static final String DESIRED_FIRMWARE = "desired_firmware";
	public static final String FIRMWARE_URI = "uri";

	private static final String IMAGE_STATE = "image_state";
	private static final Long DOWNLOADED = 2L;

	private Registration registration;
	private JSONObject device;

	private String desiredFirmware;
	private String firmwareUri;

	public AutomaticFirmwareUpdate(Registration registration, JSONObject device) {
		this.registration = registration;
		this.device = device;

	}

	public Map<String, String> download() {

		if (check()) {

			Map<String, String> map = new HashMap<>();
			map.put(DESIRED_FIRMWARE, desiredFirmware);
			map.put(FIRMWARE_URI, firmwareUri);

			return map;

		}

		return null;

	}

	public boolean applyImage(JSONObject state) {

		if (check() && state.has(IMAGE_STATE)) {
			return state.getLong(IMAGE_STATE) == DOWNLOADED;
		}

		return false;

	}

	private boolean check() {

		logger.debug("Checking if device " + registration.getEndpoint() + " supports automatic firmware update");

		desiredFirmware = DeviceAttrUtils.getStringAttr(DESIRED_FIRMWARE, "static_value", device);
		if (desiredFirmware != null) {
			firmwareUri = DeviceAttrUtils.getStringMetaAttr(DESIRED_FIRMWARE, "static_value", FIRMWARE_URI, device);
		}

		boolean result = (firmwareUri != null && !firmwareUri.isEmpty() && desiredFirmware != null
				&& !desiredFirmware.isEmpty());

		if (result) {
			logger.debug("The device " + registration.getEndpoint() + " supports automatic firmware update");
		} else {
			logger.debug("The device " + registration.getEndpoint() + " does not support automatic firmware update");
		}

		return result;
	}
}
