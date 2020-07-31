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

package de.symeda.sormas.ui.caze;

import com.vaadin.v7.data.validator.AbstractValidator;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;

public class GermanCaseClassificationValidator extends AbstractValidator<CaseClassification> {

	private final String caseUuid;

	public GermanCaseClassificationValidator(String caseUuid, String errorMessage) {
		super(errorMessage);
		this.caseUuid = caseUuid;
	}

	@Override
	protected boolean isValidValue(CaseClassification caseClassification) {
		return de.symeda.sormas.api.caze.GermanCaseClassificationValidator.isValidGermanCaseClassification(
			caseClassification,
			FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid),
			FacadeProvider.getCaseFacade().hasPositiveLabResult(caseUuid));
	}

	@Override
	public Class<CaseClassification> getType() {
		return CaseClassification.class;
	}
}
