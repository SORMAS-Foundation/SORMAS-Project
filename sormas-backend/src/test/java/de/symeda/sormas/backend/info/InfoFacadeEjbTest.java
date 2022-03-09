/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.info;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Test;

import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

public class InfoFacadeEjbTest extends AbstractBeanTest {

	private String originalCustomFilesPath;

	@Override
	public void init() {
		super.init();

		originalCustomFilesPath = MockProducer.getProperties().getProperty(ConfigFacadeEjb.CUSTOM_FILES_PATH);
		if (originalCustomFilesPath == null) {
			originalCustomFilesPath = "";
		}

		try {
			MockProducer.getProperties()
				.setProperty(
					ConfigFacadeEjb.CUSTOM_FILES_PATH,
					Paths.get(getClass().getResource("/").toURI()).toAbsolutePath().toString() + "/dataDictionaryTestCustom");
		} catch (URISyntaxException e) {
			throw new RuntimeException("Could not set custom files path", e);
		}

		UserDto admin = creator.createUser(creator.createRDCF(), UserRole.ADMIN);
		loginWith(admin);
	}

	@After
	public void destroy() {
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.CUSTOM_FILES_PATH, originalCustomFilesPath);
	}

	@Test
	public void testDataDictionaryAllowed() {
		assertThat(getInfoFacade().isGenerateDataProtectionDictionaryAllowed(), is(true));
	}
}
