/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.environment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.environment.EnvironmentCriteria;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;

public class EnvironmentServiceTest extends AbstractBeanTest {

	@Test
	public void testGetSimilarEnvironmentUuid() {

		RDCF rdcf = creator.createRDCF();
		RDCF rdcf2 = creator.createRDCF("Region2", "District2", "Community2", "Facility2");

		EnvironmentDto environment1 = creator.createEnvironment("Environment1", EnvironmentMedia.AIR, nationalAdmin.toReference(), rdcf, e -> {
			e.getLocation().setLatitude(20D);
			e.getLocation().setLongitude(20D);
			e.setExternalId("12345");
		});
		EnvironmentDto environment2 = creator.createEnvironment("Environment2", EnvironmentMedia.WATER, nationalAdmin.toReference(), rdcf, e -> {
			e.getLocation().setLatitude(30D);
			e.getLocation().setLongitude(30D);
		});

		EnvironmentCriteria criteria = new EnvironmentCriteria().environmentMedia(EnvironmentMedia.AIR);
		// GPS coordinates missing from criteria
		assertNull(getEnvironmentService().getSimilarEnvironmentUuid(criteria));
		criteria.gpsLatFrom(20D).gpsLatTo(20D).gpsLonFrom(20D).gpsLonTo(20D);
		// Environment media and GPS coordinates specified
		assertEquals(environment1.getUuid(), getEnvironmentService().getSimilarEnvironmentUuid(criteria));
		criteria.setExternalId("678910");
		// External ID does not match, overrides everything else
		assertNull(getEnvironmentService().getSimilarEnvironmentUuid(criteria));
		criteria.externalId("12345").gpsLatFrom(50D).gpsLatTo(50D).gpsLonFrom(50D).gpsLonTo(50D).environmentMedia(EnvironmentMedia.WATER);
		// External ID matches, overrides everything else
		assertEquals(environment1.getUuid(), getEnvironmentService().getSimilarEnvironmentUuid(criteria));
		criteria.gpsLatFrom(30D).gpsLatTo(30D).gpsLonFrom(30D).gpsLonTo(30D).externalId(null).region(rdcf2.region).district(rdcf2.district);
		// Region and district specified for both, but do not match
		assertNull(getEnvironmentService().getSimilarEnvironmentUuid(criteria));
		criteria.region(rdcf.region).district(rdcf.district);
		// Region and district specified for both and matching
		assertEquals(environment2.getUuid(), getEnvironmentService().getSimilarEnvironmentUuid(criteria));
	}
}
