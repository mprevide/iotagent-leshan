package org.cpqd.iotagent.lwm2m.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertSame;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

public class ResourceBlackListMgmtTest {

	@Test
	public void shouldImplementSingletonPattern() {

		ResourceBlackListMgmt i1 = ResourceBlackListMgmt.getInstance();
		ResourceBlackListMgmt i2 = ResourceBlackListMgmt.getInstance();
		ResourceBlackListMgmt i3 = ResourceBlackListMgmt.getInstance();

		assertSame(i1, i2);
		assertSame(i1, i3);

	}

	@Test
	public void shouldLoadResourcesBlackList() {

		// given
		String resource = "/0/0/0";
		Map<String, String> result = new LinkedHashMap<>();
		result.put(resource, "");

		// when
		ResourceBlackListMgmt resourceBlackListMgmt = ResourceBlackListMgmt.getInstance();

		// then
		assertNotNull(resourceBlackListMgmt);

	}

	@Test
	public void shouldReturnTrueWhenTheValueIsBlackListed() {

		String resource = "/40000/0/0";
		assertTrue(ResourceBlackListMgmt.getInstance().isBlackListed(resource, ""));
	}

	@Test
	public void shouldReturnFalseWhenResourceWasNotDefined() {

		String resource = "/5/0/1";
		assertFalse(ResourceBlackListMgmt.getInstance().isBlackListed(resource, ""));
	}

	@Test
	public void shouldReturnFalseWhenValueIsNotBlackListed() {

		String resource = "/0/0/0";
		assertFalse(ResourceBlackListMgmt.getInstance().isBlackListed(resource, "85.3"));
	}

}
