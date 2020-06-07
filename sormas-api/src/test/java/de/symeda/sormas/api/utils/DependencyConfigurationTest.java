/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
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
	public void testGetChildren() {

		assertArrayEquals(
			new String[] {
				"testB",
				"testC" },
			DependencyConfiguration.getChildren(TestClass.class, "testA").toArray());
		assertArrayEquals(
			new String[] {
				"testD" },
			DependencyConfiguration.getChildren(TestClass.class, "testC").toArray());
		assertArrayEquals(new String[] {}, DependencyConfiguration.getChildren(TestClass.class, "testB").toArray());
	}

	@Test
	public void testGetParent() {

		assertEquals("testA", DependencyConfiguration.getParent(TestClass.class, "testB"));
		assertEquals("testA", DependencyConfiguration.getParent(TestClass.class, "testC"));
		assertEquals("testC", DependencyConfiguration.getParent(TestClass.class, "testD"));
		assertNull(DependencyConfiguration.getParent(TestClass.class, "testA"));
	}
}
