package de.symeda.sormas.api.therapy;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.caze.CaseCriteria;

@Remote
public interface TreatmentFacade {
	
	List<TreatmentIndexDto> getIndexList(TreatmentCriteria criteria);
	
	TreatmentDto getTreatmentByUuid(String uuid);
	
	TreatmentDto saveTreatment(TreatmentDto treatment);
	
	void deleteTreatment(String treatmentUuid, String userUuid);
	
	List<TreatmentDto> getAllActiveTreatmentsAfter(Date date, String userUuid);
	
	List<TreatmentDto> getByUuids(List<String> uuids);
	
	List<String> getAllActiveUuids(String userUuid);
	
	List<TreatmentExportDto> getExportList(String userUuid, CaseCriteria criteria, int first, int max);

}
