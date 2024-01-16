/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.caze;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseIndexDetailedDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.person.SymptomJournalStatus;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class CaseIndexDetailedDtoResultTransformer implements ResultTransformer {

	private static final long serialVersionUID = -4616187250169484589L;

	@Override
	public CaseIndexDetailedDto transformTuple(Object[] tuple, String[] aliases) {
		int index = -1;

		//@formatter:off
        return new CaseIndexDetailedDto((Long)tuple[++index], (String)tuple[++index], (String)tuple[++index], (String)tuple[++index], (String)tuple[++index], (String)tuple[++index], (String)tuple[++index], (String)tuple[++index], (String)tuple[++index], (Disease)tuple[++index],
                (DiseaseVariant) tuple[++index], (String)tuple[++index], (CaseClassification)tuple[++index], (InvestigationStatus)tuple[++index],
                (PresentCondition)tuple[++index], (Date)tuple[++index], (Date)tuple[++index], (String)tuple[++index],
                (String)tuple[++index], (String)tuple[++index], (String)tuple[++index], (String)tuple[++index],
                (String)tuple[++index], (String)tuple[++index], (String)tuple[++index], (String)tuple[++index], (CaseOutcome) tuple[++index],
                (Integer)tuple[++index], (ApproximateAgeType) tuple[++index], (Integer)tuple[++index], (Integer)tuple[++index], (Integer)tuple[++index], (Sex) tuple[++index], (Date)tuple[++index],
                (Float)tuple[++index], (FollowUpStatus) tuple[++index], (Date)tuple[++index], (SymptomJournalStatus) tuple[++index], (VaccinationStatus) tuple[++index],
                (Date)tuple[++index], (Long)tuple[++index],
                (String)tuple[++index], (String)tuple[++index], (String)tuple[++index], (DeletionReason)tuple[++index], (String)tuple[++index], (Boolean)tuple[++index],
                (YesNoUnknown) tuple[++index], (String)tuple[++index], (String)tuple[++index], (String)tuple[++index], (String)tuple[++index], (String)tuple[++index], (String)tuple[++index],
                (String)tuple[++index], (String)tuple[++index], (String)tuple[++index], (Date)tuple[++index],
                (String)tuple[++index], (String)tuple[++index],
                (int)tuple[++index], (long)tuple[++index], (Date)tuple[++index], (long)tuple[++index]
		);
        //@formatter:on
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List transformList(List list) {
		return list;
	}
}
