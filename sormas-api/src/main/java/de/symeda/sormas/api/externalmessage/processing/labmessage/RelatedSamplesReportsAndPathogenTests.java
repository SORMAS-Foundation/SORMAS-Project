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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;

public class RelatedSamplesReportsAndPathogenTests {

	private Map<SampleReportDto, SampleDto> relatedSampleReports = Collections.checkedMap(new HashMap<>(), SampleReportDto.class, SampleDto.class);
	private List<PathogenTestDto> pathogenTests = new ArrayList<>();

	public Map<SampleReportDto, SampleDto> getRelatedSampleReportsWithSamples() {
		return this.relatedSampleReports;
	}

	public List<PathogenTestDto> getPathogenTests() {
		return pathogenTests;
	}

	private void addPathogenTest(PathogenTestDto pathogenTest) {
		pathogenTests.add(pathogenTest);
	}

	public void add(SampleReportDto sampleReport, SampleAndPathogenTests sampleAndPathogenTests) {
		if (sampleAndPathogenTests.getSample() == null) {
			throw new IllegalArgumentException("Sample to add must not be null.");
		}
		relatedSampleReports.put(sampleReport, sampleAndPathogenTests.getSample());

		if (sampleAndPathogenTests.getPathogenTests() != null) {
			sampleAndPathogenTests.getPathogenTests().forEach(this::addPathogenTest);
		}

	}

}
