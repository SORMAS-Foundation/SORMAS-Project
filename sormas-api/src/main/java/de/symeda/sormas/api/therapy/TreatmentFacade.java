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
	
	void deleteTreatment(String treatmentUuid);
	
	List<TreatmentDto> getAllActiveTreatmentsAfter(Date date);
	
	List<TreatmentDto> getByUuids(List<String> uuids);
	
	List<String> getAllActiveUuids();
	
	List<TreatmentExportDto> getExportList(CaseCriteria criteria, int first, int max);

}
