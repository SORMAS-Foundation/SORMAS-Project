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
package de.symeda.sormas.backend.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.mockito.Mockito;

import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;

import java.time.Duration;

public class ConfigFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testValidateExternalUrls() {
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_SYMPTOM_JOURNAL_URL, "https://www.google.com");
		getConfigFacade().validateConfigUrls();

		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_SYMPTOM_JOURNAL_URL, "http://www.google.com");
		getConfigFacade().validateConfigUrls();

		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_SYMPTOM_JOURNAL_URL, "http://my-docker-service:12345/route/path");
		getConfigFacade().validateConfigUrls();

		try {
			MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_SYMPTOM_JOURNAL_URL, "htps://www.google.com#");
		} catch (IllegalArgumentException ignored) {
		}
	}

	@Test
	public void testValidateAppUrls() {

		Mockito.when(InfoProvider.get().getVersion()).thenReturn("0.7.0");
		Mockito.when(InfoProvider.get().getMinimumRequiredVersion()).thenReturn("0.5.0");
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.APP_URL, "https://www.sormas.org/downloads/sormas-0.7.0-release.apk");
		getConfigFacade().validateAppUrls();

		MockProducer.getProperties().setProperty(ConfigFacadeEjb.APP_URL, "https://www.sormas.org/downloads-0.4.0-test/sormas-0.7.0-release.apk");
		getConfigFacade().validateAppUrls();

		MockProducer.getProperties().setProperty(ConfigFacadeEjb.APP_URL, "https://www.sormas.org/downloads/sormas-release.apk");
		try {
			getConfigFacade().validateAppUrls();
			fail();
		} catch (IllegalArgumentException e) {
		}

		MockProducer.getProperties().setProperty(ConfigFacadeEjb.APP_URL, "https://www.sormas.org/downloads/sormas-0.4.0-release.apk");
		try {
			getConfigFacade().validateAppUrls();
			fail();
		} catch (IllegalArgumentException e) {
		}

		MockProducer.getProperties().setProperty(ConfigFacadeEjb.APP_URL, "https://www.sormas.org/downloads/sormas-0.8.0-release.apk");
		try {
			getConfigFacade().validateAppUrls();
			fail();
		} catch (IllegalArgumentException e) {
		}

		Mockito.when(InfoProvider.get().getVersion()).thenReturn("1.0.0");
		getConfigFacade().validateAppUrls();

		MockProducer.getProperties().setProperty(ConfigFacadeEjb.APP_LEGACY_URL, "https://www.sormas.org/downloads/sormas-0.8.0-release.apk");
		try {
			getConfigFacade().validateAppUrls();
			fail();
		} catch (IllegalArgumentException e) {
		}

		MockProducer.getProperties().setProperty(ConfigFacadeEjb.APP_LEGACY_URL, "https://www.sormas.org/downloads/sormas-0.7.0-release.apk");
		getConfigFacade().validateAppUrls();

		MockProducer.getProperties().setProperty(ConfigFacadeEjb.APP_URL, "https://www.sormas.org/downloads/sormas-1.0.0-release.apk");
		getConfigFacade().validateAppUrls();

		// below minimum
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.APP_LEGACY_URL, "https://www.sormas.org/downloads/sormas-0.4.0-release.apk");
		try {
			getConfigFacade().validateAppUrls();
			fail();
		} catch (IllegalArgumentException e) {
		}

	}

	@Test
	public void testNormalizeLocaleString() {

		assertThat(ConfigFacadeEjb.normalizeLocaleString("  "), isEmptyString());
		assertThat(ConfigFacadeEjb.normalizeLocaleString("en"), is("en"));
		assertThat(ConfigFacadeEjb.normalizeLocaleString("En"), is("en"));
		assertThat(ConfigFacadeEjb.normalizeLocaleString("en-CA"), is("en-CA"));
		assertThat(ConfigFacadeEjb.normalizeLocaleString("en-cA"), is("en-CA"));
	}

	@Test
	/*
	 * If you need to change this test to make it pass, you probably changed the behaviour of the ExternalVisitsResource.
	 * Please note that other system used alongside with SORMAS are depending on this, so that their developers must be notified of any
	 * relevant API changes some time before they go into any test and productive system. Please inform the SORMAS core development team at
	 * https://gitter.im/SORMAS-Project!
	 */
	public void testPatientDiaryConfigTokenLifetime() {
		// property not specified
		assertThat(getConfigFacade().getPatientDiaryConfig().getTokenLifetime(), equalTo(Duration.ofSeconds(21600L)));

		// property specifies empty value
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_TOKEN_LIFETIME, "");
		assertThat(getConfigFacade().getPatientDiaryConfig().getTokenLifetime(), equalTo(Duration.ofSeconds(21600L)));

		// property specifies zero
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_TOKEN_LIFETIME, "0");
		assertThat(getConfigFacade().getPatientDiaryConfig().getTokenLifetime(), equalTo(Duration.ofSeconds(0L)));

		// property specifies value > 0
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.INTERFACE_PATIENT_DIARY_TOKEN_LIFETIME, "666");
		assertThat(getConfigFacade().getPatientDiaryConfig().getTokenLifetime(), equalTo(Duration.ofSeconds(666L)));
	}
}
