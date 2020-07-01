package org.cpqd.iotagent;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.cpqd.iotagent.lwm2m.utils.DeviceAttrUtils;
import org.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

/**
 * Help listeners take decisions about automatic firmware update.
 */
public class AutomaticFirmwareUpdate {

	private Logger logger = Logger.getLogger(AutomaticFirmwareUpdate.class);

	public static final String DESIRED_FIRMWARE = "desired_firmware";
	public static final String FIRMWARE_URI = "uri";
	public static final String NOTES = "description";
	public static final String MANDATORY = "mandatory";
	public static final String IMAGE_ID = "image_id";

	public static final String LABEL = "label";
	private static final String IMAGE_STATE = "image_state";
	private static final Long DOWNLOADED = 2L;
	private static final String STATIC_VALUE = "static_value";

	private JSONObject device;

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
			return getFirmwareUpdateMap();
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

		Map<String, String> firmwareUpdateMap = getFirmwareUpdateMap();

		boolean result = !firmwareUpdateMap.containsValue(null);

		if (result) {
			logger.debug("The device " + device.getString(LABEL) + " supports automatic firmware update");
		} else {
			logger.debug("The device " + device.getString(LABEL) + " does not support automatic firmware update");
		}

		return result;
	}

	/**
	 * 
	 * @return a map with all necessary information to download and install the
	 *         firmware automatically.
	 */
	private Map<String, String> getFirmwareUpdateMap() {

		String desiredFirmware = null;
		String uri = null;
		String notes = null;
		String mandatory = null;
		String imageId = null;

		desiredFirmware = DeviceAttrUtils.getStringAttr(DESIRED_FIRMWARE, STATIC_VALUE, device);

		if (desiredFirmware != null) {
			// collect meta data
			uri = DeviceAttrUtils.getStringMetaAttr(DESIRED_FIRMWARE, STATIC_VALUE, FIRMWARE_URI, device);
			notes = DeviceAttrUtils.getStringMetaAttr(DESIRED_FIRMWARE, STATIC_VALUE, NOTES, device);
			mandatory = DeviceAttrUtils.getStringMetaAttr(DESIRED_FIRMWARE, STATIC_VALUE, MANDATORY, device);
			imageId = DeviceAttrUtils.getStringMetaAttr(DESIRED_FIRMWARE, STATIC_VALUE, IMAGE_ID, device);
		}

		Map<String, String> map = new HashMap<>();
		map.put(DESIRED_FIRMWARE, StringUtils.isBlank(desiredFirmware) ? null : desiredFirmware);
		map.put(FIRMWARE_URI, StringUtils.isBlank(uri) ? null : uri);
		map.put(NOTES, StringUtils.isBlank(notes) ? "" : Base64.encodeBase64String(notes.getBytes()));
		map.put(MANDATORY, "true".equals(mandatory) ? "t" : "f");
		map.put(IMAGE_ID, StringUtils.isBlank(imageId) ? null : imageId);

		return map;

	}
}
