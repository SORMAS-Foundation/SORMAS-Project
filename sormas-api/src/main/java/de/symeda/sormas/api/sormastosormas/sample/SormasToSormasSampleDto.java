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

package de.symeda.sormas.api.sormastosormas.sample;

import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEntityDto;
import de.symeda.sormas.api.sormastosormas.externalmessage.SormasToSormasExternalMessageDto;

public class SormasToSormasSampleDto extends SormasToSormasEntityDto<SampleDto> {

	private static final long serialVersionUID = 5867733293483599601L;

	@Valid
	private final List<PathogenTestDto> pathogenTests;

	@Valid
	private final List<AdditionalTestDto> additionalTests;

	@Valid
	private final List<SormasToSormasExternalMessageDto> externalMessages;

	public SormasToSormasSampleDto() {
		super();

		this.pathogenTests = Collections.emptyList();
		this.additionalTests = Collections.emptyList();
		this.externalMessages = Collections.emptyList();
	}

	public SormasToSormasSampleDto(
		SampleDto sample,
		List<PathogenTestDto> pathogenTests,
		List<AdditionalTestDto> additionalTests,
		List<SormasToSormasExternalMessageDto> externalMessages) {
		super(sample);

		this.pathogenTests = pathogenTests;
		this.additionalTests = additionalTests;
		this.externalMessages = externalMessages;
	}

	public List<PathogenTestDto> getPathogenTests() {
		return pathogenTests;
	}

	public List<AdditionalTestDto> getAdditionalTests() {
		return additionalTests;
	}

	public List<SormasToSormasExternalMessageDto> getExternalMessages() {
		return externalMessages;
	}
}
