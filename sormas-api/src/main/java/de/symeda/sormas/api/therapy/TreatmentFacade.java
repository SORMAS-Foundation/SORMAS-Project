package de.symeda.sormas.api.therapy;

import java.util.List;

import javax.ejb.Remote;

@Remote
public interface TreatmentFacade {
	
	List<TreatmentIndexDto> getIndexList(TreatmentCriteria criteria);
	
	TreatmentDto getTreatmentByUuid(String uuid);
	
	TreatmentDto saveTreatment(TreatmentDto treatment);
	
	void deleteTreatment(String treatmentUuid, String userUuid);

}
