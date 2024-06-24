/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.selfreport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Date;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.selfreport.SelfReportDto;
import de.symeda.sormas.api.selfreport.SelfReportType;
import de.symeda.sormas.backend.AbstractBeanTest;

public class SelfReportFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetByUuid() {
		SelfReportDto dto = createSelfReport();

		SelfReportDto result = getSelfReportFacade().getByUuid(dto.getUuid());

		assertThat(result, notNullValue());
		assertThat(result.getUuid(), is(dto.getUuid()));
	}

	private SelfReportDto createSelfReport() {
		SelfReportDto dto = SelfReportDto.build(SelfReportType.CASE);
		dto.setReportDate(new Date());
		dto.setDisease(Disease.CORONAVIRUS);
		dto.setFirstName("John");
		dto.setLastName("Doe");
		dto.setSex(Sex.MALE);

		return getSelfReportFacade().save(dto);
	}
}
