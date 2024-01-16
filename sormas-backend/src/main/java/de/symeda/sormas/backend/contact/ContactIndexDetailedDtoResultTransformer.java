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

package de.symeda.sormas.backend.contact;

import java.util.Date;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.contact.ContactCategory;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactIndexDetailedDto;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.person.SymptomJournalStatus;

public class ContactIndexDetailedDtoResultTransformer implements ResultTransformer {

	private static final long serialVersionUID = -6779500917075910677L;

	@Override
	public ContactIndexDetailedDto transformTuple(Object[] tuple, String[] aliases) {
		int index = -1;

		//@formatter:off
        return new ContactIndexDetailedDto(
                (String) tuple[++index], (String) tuple[++index], (String) tuple[++index], (String) tuple[++index], (String) tuple[++index],
                (Disease) tuple[++index], (String) tuple[++index], (String) tuple[++index], (String) tuple[++index], (String) tuple[++index],
                (String) tuple[++index], (Date) tuple[++index], (ContactCategory) tuple[++index],
                (ContactProximity) tuple[++index], (ContactClassification) tuple[++index], (ContactStatus) tuple[++index], (Float) tuple[++index],
                (FollowUpStatus) tuple[++index], (Date) tuple[++index], (SymptomJournalStatus) tuple[++index], (VaccinationStatus) tuple[++index], (String) tuple[++index],
                (String) tuple[++index], (Date) tuple[++index],
                (CaseClassification) tuple[++index], (String) tuple[++index], (String) tuple[++index],
                (Date) tuple[++index],
                (String) tuple[++index], (String) tuple[++index], (String) tuple[++index], (DeletionReason) tuple[++index], (String) tuple[++index], (Boolean) tuple[++index], (Boolean) tuple[++index],
                (Sex) tuple[++index], (Integer) tuple[++index], (ApproximateAgeType) tuple[++index],
                (String) tuple[++index], (String) tuple[++index], (String) tuple[++index], (String) tuple[++index], (String) tuple[++index], (String) tuple[++index],
                (String) tuple[++index], (String) tuple[++index], (ContactRelation) tuple[++index], (Integer) tuple[++index]
                );
        //@formatter:on
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List transformList(List list) {
		return list;
	}
}
