package de.symeda.sormas.api.clinicalcourse;

import java.util.List;

import javax.ejb.Remote;

@Remote
public interface ClinicalVisitFacade {

	List<ClinicalVisitIndexDto> getIndexList(ClinicalVisitCriteria criteria);
	
	ClinicalVisitDto getClinicalVisitByUuid(String uuid);
	
	ClinicalVisitDto saveClinicalVisit(ClinicalVisitDto clinicalVisit, String caseUuid);
	
	void deleteClinicalVisit(String clinicalVisitUuid, String userUuid);
	
}
