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

package de.symeda.sormas.api.externalmessage.processing.labmessage;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageMapper;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.UserDto;

/**
 * Collection of common non UI related functions used by processing related code placed in multiple classes
 */
public class LabMessageProcessingHelper {

	private LabMessageProcessingHelper() {
	}

	public static List<PathogenTestDto> buildPathogenTests(
		SampleDto sample,
		int sampleReportIndex,
		ExternalMessageDto labMessage,
		ExternalMessageMapper mapper,
		UserDto user) {
		ArrayList<PathogenTestDto> pathogenTests = new ArrayList<>();
		for (TestReportDto testReport : labMessage.getSampleReportsNullSafe().get(sampleReportIndex).getTestReports()) {
			pathogenTests.add(buildPathogenTest(testReport, mapper, sample, user));
		}

		return pathogenTests;
	}

	public static PathogenTestDto buildPathogenTest(TestReportDto testReport, ExternalMessageMapper mapper, SampleDto sample, UserDto user) {
		PathogenTestDto pathogenTest = PathogenTestDto.build(sample, user);
		mapper.mapToPathogenTest(testReport, pathogenTest);

		return pathogenTest;
	}
}
