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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.Diseases.DiseasesConfiguration;

public class DiseasesConfigurationTest {

	public static class TestClass {

		@SuppressWarnings("unused")
		private String testNone;

		@Diseases()
		@SuppressWarnings("unused")
		private String testEmpty;

		@Diseases(Disease.EVD)
		@SuppressWarnings("unused")
		private String testOne;

		@Diseases({
			Disease.CHOLERA,
			Disease.CSM })
		@SuppressWarnings("unused")
		private String testMultiple;

		@Diseases(value = {
			Disease.CHOLERA,
			Disease.EVD }, hide = true)
		@SuppressWarnings("unused")
		private String testHide;
	}

	@Test
	public void testIsMissing() {

		assertTrue(DiseasesConfiguration.isMissing(TestClass.class, "testNone"));
		assertFalse(DiseasesConfiguration.isMissing(TestClass.class, "testEmpty"));
		assertFalse(DiseasesConfiguration.isMissing(TestClass.class, "testOne"));
		assertFalse(DiseasesConfiguration.isMissing(TestClass.class, "testMultiple"));
	}

	@Test
	public void testIsDefined() {

		assertFalse(DiseasesConfiguration.isDefined(TestClass.class, "testNone", Disease.NEW_INFLUENZA));
		assertFalse(DiseasesConfiguration.isDefined(TestClass.class, "testEmpty", Disease.NEW_INFLUENZA));
		assertFalse(DiseasesConfiguration.isDefined(TestClass.class, "testOne", Disease.NEW_INFLUENZA));
		assertTrue(DiseasesConfiguration.isDefined(TestClass.class, "testOne", Disease.EVD));
		assertFalse(DiseasesConfiguration.isDefined(TestClass.class, "testMultiple", Disease.NEW_INFLUENZA));
		assertTrue(DiseasesConfiguration.isDefined(TestClass.class, "testMultiple", Disease.CHOLERA));
		assertTrue(DiseasesConfiguration.isDefined(TestClass.class, "testMultiple", Disease.CSM));
	}

	@Test
	public void testHide() {

		assertTrue(DiseasesConfiguration.isDefined(TestClass.class, "testHide", Disease.NEW_INFLUENZA));
		assertFalse(DiseasesConfiguration.isDefined(TestClass.class, "testHide", Disease.CHOLERA));
	}

}
