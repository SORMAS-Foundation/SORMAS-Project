package de.symeda.sormas.api.contact;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.MapCaseDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.visit.VisitReferenceDto;

@Remote
public interface ContactFacade {

	List<ContactDto> getAllContactsAfter(Date date, String userUuid);
	
	List<ContactDto> getFollowUpBetween(Date fromDate, Date toDate, DistrictReferenceDto districtRef, Disease disease, String userUuid);
	
	ContactDto getContactByUuid(String uuid);
    
	ContactDto saveContact(ContactDto dto);
	
	List<ContactReferenceDto> getSelectableContacts(UserReferenceDto user);

	ContactReferenceDto getReferenceByUuid(String uuid);

	List<String> getAllUuids(String userUuid);

	void generateContactFollowUpTasks();

	List<ContactDto> getByUuids(List<String> uuids);
	
	List<MapContactDto> getContactsForMap(RegionReferenceDto regionRef, DistrictReferenceDto districtRef, Disease disease, Date fromDate, Date toDate, String userUuid, List<MapCaseDto> mapCaseDtos);
	
	void deleteContact(ContactReferenceDto contactRef, String userUuid);
	
	List<ContactIndexDto> getIndexList(String userUuid, ContactCriteria contactCriteria);
	
	List<ContactReferenceDto> getAllByVisit(VisitReferenceDto visitRef);

	List<ContactExportDto> getExportList(String userUuid, ContactCriteria contactCriteria);
	
	List<DashboardContactDto> getContactsForDashboard(RegionReferenceDto regionRef, DistrictReferenceDto districtRef, Disease disease, Date from, Date to, String userUuid);
	
	Map<ContactStatus, Long> getNewContactCountPerStatus(ContactCriteria contactCriteria, String userUuid);

	Map<ContactClassification, Long> getNewContactCountPerClassification(ContactCriteria contactCriteria, String userUuid);
	
	Map<FollowUpStatus, Long> getNewContactCountPerFollowUpStatus(ContactCriteria contactCriteria, String userUuid);
}
