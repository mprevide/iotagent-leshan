package org.cpqd.iotagent.lwm2m.utils;

import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

public class FileReaderPropertiesUtilTest {

	@Test
	public void shouldLoadPropertiesFile() throws IOException {
		String file = "resourceBlackList.properties";
		Properties prop = FileReaderPropertiesUtil.read(file);

		assertNotNull(prop);

	}

	@Test(expected = FileNotFoundException.class)
	public void shouldThrowErrorWhenUseInvalidFile() throws IOException {
		String file = "invalidFile.properties";
		FileReaderPropertiesUtil.read(file);
	}

}
