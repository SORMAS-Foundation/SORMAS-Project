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

package de.symeda.sormas.ui.samples.humansample;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.ui.ControllerProvider;

public class SampleEditPathogenTestListHandler {

	private final List<PathogenTestDto> pathogenTests;

	public SampleEditPathogenTestListHandler() {
		pathogenTests = new ArrayList<>();
	}

	public void addPathogenTest(PathogenTestDto pathogenTest) {
		pathogenTests.add(pathogenTest);
	}

	public List<PathogenTestDto> getPathogenTests() {
		return pathogenTests;
	}

	public void saveAll(SampleReferenceDto sample) {
		ControllerProvider.getPathogenTestController().savePathogenTests(pathogenTests, sample, true);
	}
}
