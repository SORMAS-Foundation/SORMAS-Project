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

package de.symeda.sormas.ui.externalmessage.labmessage.processing;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.externalmessage.ExternalMessageMapper;

/**
 * Collection of common non UI related functions used by processing related code placed in multiple classes
 */
public class LabMessageProcessingHelper {

	private LabMessageProcessingHelper() {
	}

	public static List<PathogenTestDto> buildPathogenTests(SampleDto sample, int sampleReportIndex, ExternalMessageDto labMessage, UserDto user) {
		ArrayList<PathogenTestDto> pathogenTests = new ArrayList<>();
		for (TestReportDto testReport : labMessage.getSampleReportsNullSafe().get(sampleReportIndex).getTestReports()) {
			pathogenTests.add(buildPathogenTest(testReport, labMessage, sample, user));
		}

		return pathogenTests;
	}

	public static PathogenTestDto buildPathogenTest(TestReportDto testReport, ExternalMessageDto labMessage, SampleDto sample, UserDto user) {
		PathogenTestDto pathogenTest = PathogenTestDto.build(sample, user);
		ExternalMessageMapper.forLabMessage(labMessage).mapToPathogenTest(testReport, pathogenTest);

		return pathogenTest;
	}

	public static void updateAddressAndSavePerson(PersonDto personDto, ExternalMessageDto labMessageDto) {
		if (personDto.getAddress().getCity() == null
			&& personDto.getAddress().getHouseNumber() == null
			&& personDto.getAddress().getPostalCode() == null
			&& personDto.getAddress().getStreet() == null) {
			ExternalMessageMapper.forLabMessage(labMessageDto).mapToLocation(personDto.getAddress());
		}
		FacadeProvider.getPersonFacade().save(personDto);
	}
}
