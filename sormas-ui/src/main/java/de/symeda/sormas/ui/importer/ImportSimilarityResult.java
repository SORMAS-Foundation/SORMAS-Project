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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.importer;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonIndexDto;

public class ImportSimilarityResult {

	private final PersonIndexDto matchingPerson;
	private final CaseDataDto matchingCase;
	private final boolean usePerson;
	private final boolean useCase;
	private final boolean skip;
	private final boolean cancelImport;
	
	public ImportSimilarityResult(PersonIndexDto matchingPerson, CaseDataDto matchingCase, boolean usePerson, boolean useCase,
			boolean skip, boolean cancelImport) {
		this.matchingPerson = matchingPerson;
		this.matchingCase = matchingCase;
		this.usePerson = usePerson;
		this.useCase = useCase;
		this.skip = skip;
		this.cancelImport = cancelImport;
	}

	public PersonIndexDto getMatchingPerson() {
		return matchingPerson;
	}

	public CaseDataDto getMatchingCase() {
		return matchingCase;
	}

	public boolean isUsePerson() {
		return usePerson;
	}

	public boolean isUseCase() {
		return useCase;
	}

	public boolean isSkip() {
		return skip;
	}

	public boolean isCancelImport() {
		return cancelImport;
	}
	
}
