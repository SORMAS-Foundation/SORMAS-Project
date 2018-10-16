package de.symeda.sormas.api.visit;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;

@Remote
public interface VisitFacade {

	List<VisitDto> getAllVisitsAfter(Date date, String userUuid);

	VisitDto getVisitByUuid(String uuid);

	VisitReferenceDto getReferenceByUuid(String uuid);

	VisitDto saveVisit(VisitDto dto);

	List<VisitDto> getAllByPerson(PersonReferenceDto personRef);

	List<VisitDto> getAllByContact(ContactReferenceDto contactRef);

	List<String> getAllUuids(String userUuid);

	List<VisitDto> getByUuids(List<String> uuids);
	
	void deleteVisit(VisitReferenceDto visitRef, String userUuid);
	
	int getNumberOfVisits(ContactReferenceDto contactRef, VisitStatus visitStatus);
	
	List<DashboardVisitDto> getDashboardVisitsByContact(ContactReferenceDto contactRef, Date from, Date to);
	
}
