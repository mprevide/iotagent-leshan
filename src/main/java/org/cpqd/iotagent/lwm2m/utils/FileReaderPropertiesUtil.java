package org.cpqd.iotagent.lwm2m.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FileReaderPropertiesUtil {

	private FileReaderPropertiesUtil() {

	}

	public static Properties read(String file) throws IOException {
		Properties prop = new Properties();
		InputStream inStream = ClassLoader.getSystemClassLoader().getResourceAsStream(file);
		if (inStream != null) {
			prop.load(inStream);
		} else {
			throw new FileNotFoundException("property file " + file + "not found");
		}

		return prop;
	}

}
