package org.cpqd.iotagent.lwm2m.utils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Discard invalid values emitted from devices.
 */
public class ResourceBlackListMgmt {

	private static final Logger LOGGER = Logger.getLogger(ResourceBlackListMgmt.class);
	private static ResourceBlackListMgmt instance = null;
	private Map<String, String> map = new LinkedHashMap<>();
	private static final String FILE = "resourceBlackList.properties";

	private ResourceBlackListMgmt() {
		load();
	}

	public static ResourceBlackListMgmt getInstance() {
		if (instance == null) {
			instance = new ResourceBlackListMgmt();
		}

		return instance;
	}

	private void load() {
		try {
			Properties prop = FileReaderPropertiesUtil.read(FILE);
			for (final String name : prop.stringPropertyNames())
				map.put(name, prop.getProperty(name));
		} catch (IOException e) {
			LOGGER.warn("Was not possible load resource black list");
		}

	}

	public boolean isBlackListed(String resource, String value) {
		LOGGER.debug("checking if the the value: " + value + " of resource " + resource + " is blacklisted.");
		return !map.containsKey(resource) ? Boolean.FALSE : map.get(resource).equalsIgnoreCase(value);

	}

}
