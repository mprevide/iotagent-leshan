package org.cpqd.iotagent.lwm2m.utils;

public enum LwM2MEvent {

	REGISTER("register"), UNREGISTER("unregister"), UPDATE("update");

	private final String event;

	LwM2MEvent(final String event) {
		this.event = event;
	}

	@Override
	public String toString() {
		return event;
	}

}
