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
package de.symeda.sormas.api.caze;

import java.util.Date;

import de.symeda.sormas.api.utils.ValidationException;

public class CaseLogic {

	public static void validateInvestigationDoneAllowed(CaseDataDto caze) throws ValidationException {
		if (caze.getCaseClassification() == CaseClassification.NOT_CLASSIFIED) {
			throw new ValidationException("Not allowed to set investigation status to done for an unclassified case.");
		}
	}
	
	public static Date getStartDate(Date onsetDate, Date receptionDate, Date reportDate) {
		if (onsetDate != null) {
			return onsetDate;
		} else if (receptionDate != null) {
			return receptionDate;
		} else {
			return reportDate;
		}
	}
	
}
