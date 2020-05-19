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
package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.MapCaseDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.visit.VisitSummaryExportDto;

import javax.ejb.Remote;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Remote
public interface ContactFacade {

	List<ContactDto> getAllActiveContactsAfter(Date date);

	ContactDto getContactByUuid(String uuid);

	Boolean isValidContactUuid(String uuid);

	ContactDto saveContact(ContactDto dto);

	ContactReferenceDto getReferenceByUuid(String uuid);

	List<String> getAllActiveUuids();

	void generateContactFollowUpTasks();

	List<ContactDto> getByUuids(List<String> uuids);

	List<MapContactDto> getContactsForMap(RegionReferenceDto regionRef, DistrictReferenceDto districtRef, Disease disease, Date fromDate, Date toDate, List<MapCaseDto> mapCaseDtos);

	void deleteContact(String contactUuid);

	List<ContactIndexDto> getIndexList(ContactCriteria contactCriteria, Integer first, Integer max,
			List<SortProperty> sortProperties);

	List<ContactIndexDetailedDto> getIndexDetailedList(ContactCriteria contactCriteria, Integer first, Integer max,
			List<SortProperty> sortProperties);

	List<ContactExportDto> getExportList(ContactCriteria contactCriteria, int first, int max, Language userLanguage);

	List<VisitSummaryExportDto> getVisitSummaryExportList(ContactCriteria contactCriteria, int first, int max, Language userLanguage);

	long countMaximumFollowUpDays(ContactCriteria contactCriteria);
	List<DashboardContactDto> getContactsForDashboard(RegionReferenceDto regionRef, DistrictReferenceDto districtRef, Disease disease, Date from, Date to);

	Map<ContactStatus, Long> getNewContactCountPerStatus(ContactCriteria contactCriteria);

	Map<ContactClassification, Long> getNewContactCountPerClassification(ContactCriteria contactCriteria);

	Map<FollowUpStatus, Long> getNewContactCountPerFollowUpStatus(ContactCriteria contactCriteria);

	int getFollowUpUntilCount(ContactCriteria contactCriteria);

	long count(ContactCriteria contactCriteria);

	List<String> getDeletedUuidsSince(Date since);

	boolean isDeleted(String contactUuid);

	List<ContactFollowUpDto> getContactFollowUpList(ContactCriteria contactCriteria, Date referenceDate, Integer first, Integer max,
			List<SortProperty> sortProperties);

	int[] getContactCountsByCasesForDashboard(List<Long> contactIds);

	int getNonSourceCaseCountForDashboard(List<String> caseUuids);

	void validate(ContactDto contact) throws ValidationRuntimeException;

	List<SimilarContactDto> getMatchingContacts(ContactSimilarityCriteria criteria);

	boolean isContactEditAllowed(String contactUuid);

}
