package de.symeda.sormas.api.visit;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface VisitFacade {

	List<VisitDto> getAllVisitsAfter(Date date, String userUuid);

	VisitDto getVisitByUuid(String uuid);

	VisitReferenceDto getReferenceByUuid(String uuid);

	VisitDto saveVisit(VisitDto dto);
}
