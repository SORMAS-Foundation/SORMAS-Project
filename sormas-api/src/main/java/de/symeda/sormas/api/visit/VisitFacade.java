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
package de.symeda.sormas.api.visit;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface VisitFacade {

	List<VisitDto> getAllActiveVisitsAfter(Date date);

	VisitDto getVisitByUuid(String uuid);

	VisitReferenceDto getReferenceByUuid(String uuid);

	VisitDto saveVisit(VisitDto dto);

	void validate(VisitDto dto);

	ExternalVisitDto saveExternalVisit(ExternalVisitDto dto);

	List<String> getAllActiveUuids();

	List<VisitDto> getByUuids(List<String> uuids);
	
	void deleteVisit(String visitUuid);
	
	int getNumberOfVisits(ContactReferenceDto contactRef, VisitStatus visitStatus);
	
	List<DashboardVisitDto> getDashboardVisitsByContact(ContactReferenceDto contactRef, Date from, Date to);
	
	List<VisitIndexDto> getIndexList(VisitCriteria visitCriteria, Integer first, Integer max, List<SortProperty> sortProperties);
	
	long count(VisitCriteria visitCriteria);
	
	VisitDto getLastVisitByContact(ContactReferenceDto contactRef);
	
}
