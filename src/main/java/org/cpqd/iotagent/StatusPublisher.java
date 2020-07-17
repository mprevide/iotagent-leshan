package org.cpqd.iotagent;

import org.cpqd.iotagent.lwm2m.utils.LwM2MEvent;
import org.json.JSONObject;

import br.com.dojot.IoTAgent.IoTAgent;

/**
 * Publish LW-M2M client's status
 */
public class StatusPublisher {

	IoTAgent ioTAgent;

	public StatusPublisher(IoTAgent ioTAgent) {
		this.ioTAgent = ioTAgent;
	}

	public void publish(String deviceId, String tenant, LwM2MEvent event) {

		DeviceStatus status = (LwM2MEvent.REGISTER.equals(event) ? DeviceStatus.ONLINE : DeviceStatus.OFFLINE);
		JSONObject lwM2mDeviceStatus = new JSONObject();
		lwM2mDeviceStatus.put("status", status.toString());
		lwM2mDeviceStatus.put("event", event.toString());

		ioTAgent.publishStatus(deviceId, tenant, lwM2mDeviceStatus);
	}

	public enum DeviceStatus {
		ONLINE("online"), OFFLINE("offline");

		private final String status;

		DeviceStatus(final String status) {
			this.status = status;
		}

		@Override
		public String toString() {
			return status;
		}
	}

}
