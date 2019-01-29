package de.symeda.sormas.api.therapy;

import java.util.List;

import javax.ejb.Remote;

@Remote
public interface TherapyFacade {

	List<PrescriptionIndexDto> getPrescriptionIndexList(PrescriptionCriteria criteria);
	
	List<TreatmentIndexDto> getTreatmentIndexList(TreatmentCriteria criteria);
	
	PrescriptionDto getPrescriptionByUuid(String uuid);
	
	TreatmentDto getTreatmentByUuid(String uuid);
	
	PrescriptionDto savePrescription(PrescriptionDto prescription);
	
	TreatmentDto saveTreatment(TreatmentDto treatment);
	
	void deletePrescription(String prescriptionUuid, String userUuid);
	
	void deleteTreatment(String treatmentUuid, String userUuid);
	
}
