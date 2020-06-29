package org.cpqd.iotagent;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.cpqd.iotagent.lwm2m.utils.DeviceAttrUtils;
import org.json.JSONObject;

/**
 * Help listeners take decisions about automatic firmware update.
 */
public class AutomaticFirmwareUpdate {

	private Logger logger = Logger.getLogger(AutomaticFirmwareUpdate.class);

	public static final String DESIRED_FIRMWARE = "desired_firmware";
	public static final String FIRMWARE_URI = "uri";

	public static final String LABEL = "label";
	private static final String IMAGE_STATE = "image_state";
	private static final Long DOWNLOADED = 2L;

	private JSONObject device;

	private String desiredFirmware;
	private String firmwareUri;

	public AutomaticFirmwareUpdate(JSONObject device) {
		this.device = device;

	}

	/**
	 * 
	 * @return a map with download information or null if the device does not
	 *         supports automatic firmware update.
	 */
	public Map<String, String> download() {

		if (check()) {

			Map<String, String> map = new HashMap<>();
			map.put(DESIRED_FIRMWARE, desiredFirmware);
			map.put(FIRMWARE_URI, firmwareUri);

			return map;

		}

		return null;

	}

	/**
	 * @param firmware update object state.
	 * @return a boolean describing if the device can receive an apply image
	 *         instruction.
	 */
	public boolean applyImage(JSONObject state) {

		if (check() && state.has(IMAGE_STATE)) {
			return state.getLong(IMAGE_STATE) == DOWNLOADED;
		}

		return false;

	}

	/**
	 * 
	 * @return a boolean describing if the device supports automatic firmware
	 *         update.
	 */
	private boolean check() {

		logger.debug("Checking if device " + device.getString(LABEL) + " supports automatic firmware update");
	
		desiredFirmware = DeviceAttrUtils.getStringAttr(DESIRED_FIRMWARE, "static_value", device);
		if (desiredFirmware != null) {
			firmwareUri = DeviceAttrUtils.getStringMetaAttr(DESIRED_FIRMWARE, "static_value", FIRMWARE_URI, device);
		}
	
		boolean result = (!StringUtils.isBlank(desiredFirmware) && !StringUtils.isBlank(firmwareUri));

		if (result) {
			logger.debug("The device " + device.getString(LABEL) + " supports automatic firmware update");
		} else {
			logger.debug("The device " + device.getString(LABEL) + " does not support automatic firmware update");
		}

		return result;
	}
}
