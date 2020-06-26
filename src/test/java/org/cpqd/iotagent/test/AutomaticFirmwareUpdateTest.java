package org.cpqd.iotagent.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cpqd.iotagent.AutomaticFirmwareUpdate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

public class AutomaticFirmwareUpdateTest {

	@Test
	public void shouldReturnDownloadMapWhenDeviceHasAutomaticFirmwareSupport() {

		// given

		JSONObject device = new JSONObject();
		device.put("label", "sample_device");

		List<JSONObject> meta = new ArrayList<JSONObject>();
		meta.add(new JSONObject().put("label", "uri").put("static_value", "http://<image_uri>"));

		List<JSONObject> list = new ArrayList<JSONObject>();
		list.add(new JSONObject().put("label", "desired_firmware").put("static_value", "1.0.0").put("metadata",
				new JSONArray(meta)));

		device.put("attrs", new JSONObject().put("1", new JSONArray(list)));

		// when

		Map<String, String> result = new AutomaticFirmwareUpdate(device).download();

		// then

		assertEquals("The desired firmware does not match", "1.0.0", result.get("desired_firmware"));
		assertEquals("The image uri does not match", "http://<image_uri>", result.get("uri"));

	}

	@Test
	public void shouldReturnNullIfDeviceDoesNotHaveDesiredFirmware() {

		// given

		JSONObject device = new JSONObject();
		device.put("label", "sample_device");

		List<JSONObject> list = new ArrayList<JSONObject>();
		list.add(new JSONObject().put("label", "sample_attr").put("static_value", "sample_value"));

		device.put("attrs", new JSONObject().put("1", new JSONArray(list)));

		// when

		Map<String, String> result = new AutomaticFirmwareUpdate(device).download();

		// then

		assertNull(result);

	}

	@Test
	public void shouldReturnNullIfDesiredFirmwareIsEmpty() {

		// given

		JSONObject device = new JSONObject();
		device.put("label", "sample_device");

		List<JSONObject> list = new ArrayList<JSONObject>();
		list.add(new JSONObject().put("label", "desired_firmware").put("static_value", ""));

		device.put("attrs", new JSONObject().put("1", new JSONArray(list)));

		// when

		Map<String, String> result = new AutomaticFirmwareUpdate(device).download();

		// then

		assertNull(result);

	}

	@Test
	public void shouldReturnNullIfDeviceDoesNotHaveUri() {

		// given

		JSONObject device = new JSONObject();
		device.put("label", "sample_device");

		List<JSONObject> meta = new ArrayList<JSONObject>();
		meta.add(new JSONObject().put("label", "sample_attr").put("static_value", "sample_value"));

		List<JSONObject> list = new ArrayList<JSONObject>();
		list.add(new JSONObject().put("label", "desired_firmware").put("static_value", "1.0.0").put("metadata",
				new JSONArray(meta)));

		device.put("attrs", new JSONObject().put("1", new JSONArray(list)));

		// when

		Map<String, String> result = new AutomaticFirmwareUpdate(device).download();

		// then

		assertNull(result);

	}

	@Test
	public void shouldReturnNullIfUriIsEmpty() {

		// given

		JSONObject device = new JSONObject();
		device.put("label", "sample_device");

		List<JSONObject> meta = new ArrayList<JSONObject>();
		meta.add(new JSONObject().put("label", "uri").put("static_value", ""));

		List<JSONObject> list = new ArrayList<JSONObject>();
		list.add(new JSONObject().put("label", "desired_firmware").put("static_value", "1.0.0").put("metadata",
				new JSONArray(meta)));

		device.put("attrs", new JSONObject().put("1", new JSONArray(list)));

		// when

		Map<String, String> result = new AutomaticFirmwareUpdate(device).download();

		// then

		assertNull(result);

	}

	@Test
	public void shouldReturnTrueWhenImageStateIsDownloaded() {

		// given

		JSONObject imageState = new JSONObject();
		imageState.put("image_state", 2L);

		JSONObject device = new JSONObject();
		device.put("label", "sample_device");

		List<JSONObject> meta = new ArrayList<JSONObject>();
		meta.add(new JSONObject().put("label", "uri").put("static_value", "http://<image_uri>"));

		List<JSONObject> list = new ArrayList<JSONObject>();
		list.add(new JSONObject().put("label", "desired_firmware").put("static_value", "1.0.0").put("metadata",
				new JSONArray(meta)));

		device.put("attrs", new JSONObject().put("1", new JSONArray(list)));

		// when

		boolean result = new AutomaticFirmwareUpdate(device).applyImage(imageState);

		// then

		assertTrue(result);

	}

	@Test
	public void shouldReturnFalseWhenImageStateIsNotDownloaded() {

		// given

		JSONObject imageState = new JSONObject();
		imageState.put("image_state", 3L);

		JSONObject device = new JSONObject();
		device.put("label", "sample_device");

		List<JSONObject> meta = new ArrayList<JSONObject>();
		meta.add(new JSONObject().put("label", "uri").put("static_value", "http://<image_uri>"));

		List<JSONObject> list = new ArrayList<JSONObject>();
		list.add(new JSONObject().put("label", "desired_firmware").put("static_value", "1.0.0").put("metadata",
				new JSONArray(meta)));

		device.put("attrs", new JSONObject().put("1", new JSONArray(list)));

		// when

		boolean result = new AutomaticFirmwareUpdate(device).applyImage(imageState);

		// then

		assertFalse(result);

	}

}
