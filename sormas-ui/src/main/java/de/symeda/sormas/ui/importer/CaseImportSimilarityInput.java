/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.importer;

import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.person.PersonDto;

public class CaseImportSimilarityInput {

	private final CaseDataDto caze;
	private final PersonDto person;
	private final List<CaseSelectionDto> similarCases;

	public CaseImportSimilarityInput(CaseDataDto caze, PersonDto person, List<CaseSelectionDto> similarCases) {
		this.similarCases = similarCases;
		this.caze = caze;
		this.person = person;
	}

	public List<CaseSelectionDto> getSimilarCases() {
		return similarCases;
	}

	public CaseDataDto getCaze() {
		return caze;
	}

	public PersonDto getPerson() {
		return person;
	}
}
