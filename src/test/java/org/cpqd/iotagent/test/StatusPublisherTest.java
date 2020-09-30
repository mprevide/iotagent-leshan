package org.cpqd.iotagent.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

import org.cpqd.iotagent.StatusPublisher;
import org.cpqd.iotagent.StatusPublisher.DeviceStatus;
import org.cpqd.iotagent.lwm2m.utils.LwM2MEvent;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import br.com.dojot.IoTAgent.IoTAgent;

public class StatusPublisherTest {

	@Test
	public void ShouldPublishOnlineStatusOnRegisterEvent() {

		// given

		IoTAgent ioTAgent = mock(IoTAgent.class);
		String deviceId = "e097bd";
		String tenant = "master";

		// when

		new StatusPublisher(ioTAgent).publish(deviceId, tenant, LwM2MEvent.REGISTER);

		ArgumentCaptor<JSONObject> statusCaptor = ArgumentCaptor.forClass(JSONObject.class);
		ArgumentCaptor<String> deviceIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> tenantCaptor = ArgumentCaptor.forClass(String.class);

		verify(ioTAgent, times(1)).publishStatus(deviceIdCaptor.capture(), tenantCaptor.capture(),
				statusCaptor.capture());

		// then

		assertEquals("device identification does not match", deviceId, deviceIdCaptor.getValue());
		assertEquals("tenant does not match", tenant, tenantCaptor.getValue());
		JSONObject lwM2mDeviceStatus = statusCaptor.getValue();
		assertEquals(DeviceStatus.ONLINE.toString(), lwM2mDeviceStatus.get("status"));
		assertEquals(LwM2MEvent.REGISTER.toString(), lwM2mDeviceStatus.get("event"));

	}

	@Test
	public void ShouldPublishOfflineStatusOnUnregisterEvent() {

		// given

		IoTAgent ioTAgent = mock(IoTAgent.class);
		String deviceId = "e097bd";
		String tenant = "master";

		// when

		new StatusPublisher(ioTAgent).publish(deviceId, tenant, LwM2MEvent.UNREGISTER);

		ArgumentCaptor<JSONObject> statusCaptor = ArgumentCaptor.forClass(JSONObject.class);
		ArgumentCaptor<String> deviceIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> tenantCaptor = ArgumentCaptor.forClass(String.class);

		verify(ioTAgent, times(1)).publishStatus(deviceIdCaptor.capture(), tenantCaptor.capture(),
				statusCaptor.capture());

		// then

		assertEquals("device identification does not match", deviceId, deviceIdCaptor.getValue());
		assertEquals("tenant does not match", tenant, tenantCaptor.getValue());
		JSONObject lwM2mDeviceStatus = statusCaptor.getValue();
		assertEquals(DeviceStatus.OFFLINE.toString(), lwM2mDeviceStatus.get("status"));
		assertEquals(LwM2MEvent.UNREGISTER.toString(), lwM2mDeviceStatus.get("event"));

	}

}
