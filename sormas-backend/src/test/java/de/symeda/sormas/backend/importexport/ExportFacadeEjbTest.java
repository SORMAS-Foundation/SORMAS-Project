/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.importexport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.importexport.ExportConfigurationCriteria;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.importexport.ExportType;
import de.symeda.sormas.backend.AbstractBeanTest;

public class ExportFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetExportConfigurationsWithoutCriteria() {
		creator.createExportConfiguration(
			"Case export",
			ExportType.CASE,
			new HashSet<>(Arrays.asList(CaseDataDto.UUID, CaseDataDto.CASE_CLASSIFICATION)),
			getUserFacade().getCurrentUserAsReference());
		creator.createExportConfiguration(
			"Contact export",
			ExportType.CONTACT,
			new HashSet<>(Arrays.asList(ContactDto.UUID, ContactDto.CONTACT_CATEGORY)),
			getUserFacade().getCurrentUserAsReference());

		List<ExportConfigurationDto> exportConfigurations = getExportFacade().getExportConfigurations(null, false);

		assertThat(exportConfigurations, hasSize(2));

		assertThat(exportConfigurations.get(1).getName(), is("Case export"));
		assertThat(exportConfigurations.get(1).getExportType(), is(ExportType.CASE));
		assertThat(exportConfigurations.get(1).getProperties(), hasSize(2));
	}

	@Test
	public void testGetContactExportConfigurations() {
		creator.createExportConfiguration(
			"Case export",
			ExportType.CASE,
			new HashSet<>(Arrays.asList(CaseDataDto.UUID, CaseDataDto.CASE_CLASSIFICATION)),
			getUserFacade().getCurrentUserAsReference());

		creator.createExportConfiguration(
			"Contact export",
			ExportType.CONTACT,
			new HashSet<>(Arrays.asList(ContactDto.UUID, ContactDto.CONTACT_CATEGORY)),
			getUserFacade().getCurrentUserAsReference());

		List<ExportConfigurationDto> exportConfigurations =
			getExportFacade().getExportConfigurations(new ExportConfigurationCriteria().exportType(ExportType.CONTACT), false);

		assertThat(exportConfigurations, hasSize(1));
	}
}
