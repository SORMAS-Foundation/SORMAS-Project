package de.symeda.sormas.api.clinicalcourse;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.caze.CaseCriteria;

@Remote
public interface ClinicalVisitFacade {

	List<ClinicalVisitIndexDto> getIndexList(ClinicalVisitCriteria criteria);
	
	ClinicalVisitDto getClinicalVisitByUuid(String uuid);
	
	ClinicalVisitDto saveClinicalVisit(ClinicalVisitDto clinicalVisit, String caseUuid);
	
	ClinicalVisitDto saveClinicalVisit(ClinicalVisitDto clinicalVisit);
	
	void deleteClinicalVisit(String clinicalVisitUuid);
	
	List<ClinicalVisitDto> getAllActiveClinicalVisitsAfter(Date date);
	
	List<ClinicalVisitDto> getByUuids(List<String> uuids);
	
	List<String> getAllActiveUuids();
	
	List<ClinicalVisitExportDto> getExportList(CaseCriteria criteria, int first, int max);
}
