package de.symeda.sormas.api.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.symeda.sormas.api.utils.DependantOn.DependencyConfiguration;

public class DependencyConfigurationTest {

	public class TestClass {

		@SuppressWarnings("unused")
		private String testA;

		@DependantOn("testA")
		private String testB;

		@DependantOn("testA")
		private String testC;

		@DependantOn("testC")
		private String testD;

	}

	@Test
	public void testGetChildren() throws Exception {

		assertArrayEquals(new String[] { "testB", "testC" },
				DependencyConfiguration.getChildren(TestClass.class, "testA").toArray());
		assertArrayEquals(new String[] { "testD" },
				DependencyConfiguration.getChildren(TestClass.class, "testC").toArray());
		assertArrayEquals(new String[] {}, DependencyConfiguration.getChildren(TestClass.class, "testB").toArray());
	}

	@Test
	public void testGetParent() throws Exception {

		assertEquals("testA", DependencyConfiguration.getParent(TestClass.class, "testB"));
		assertEquals("testA", DependencyConfiguration.getParent(TestClass.class, "testC"));
		assertEquals("testC", DependencyConfiguration.getParent(TestClass.class, "testD"));
		assertNull(DependencyConfiguration.getParent(TestClass.class, "testA"));
	}

}
