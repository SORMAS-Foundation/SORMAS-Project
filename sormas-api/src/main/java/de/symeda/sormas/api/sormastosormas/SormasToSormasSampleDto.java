/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.sormastosormas;

import java.io.Serializable;
import java.util.List;

import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;

public class SormasToSormasSampleDto implements Serializable {

	private static final long serialVersionUID = 5867733293483599601L;

	private SampleDto sample;

	private List<PathogenTestDto> pathogenTests;

	private List<AdditionalTestDto> additionalTests;

	public SormasToSormasSampleDto() {
	}

	public SormasToSormasSampleDto(SampleDto sample, List<PathogenTestDto> pathogenTests, List<AdditionalTestDto> additionalTests) {
		this.sample = sample;
		this.pathogenTests = pathogenTests;
		this.additionalTests = additionalTests;
	}

	public SampleDto getSample() {
		return sample;
	}

	public void setSample(SampleDto sample) {
		this.sample = sample;
	}

	public List<PathogenTestDto> getPathogenTests() {
		return pathogenTests;
	}

	public void setPathogenTests(List<PathogenTestDto> pathogenTests) {
		this.pathogenTests = pathogenTests;
	}

	public List<AdditionalTestDto> getAdditionalTests() {
		return additionalTests;
	}

	public void setAdditionalTests(List<AdditionalTestDto> additionalTests) {
		this.additionalTests = additionalTests;
	}
}
