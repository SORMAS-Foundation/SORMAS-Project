package de.symeda.sormas.api.therapy;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.caze.CaseCriteria;

@Remote
public interface PrescriptionFacade {
	
	List<PrescriptionIndexDto> getIndexList(PrescriptionCriteria criteria);
	
	PrescriptionDto getPrescriptionByUuid(String uuid);
	
	PrescriptionDto savePrescription(PrescriptionDto prescription);
	
	void deletePrescription(String prescriptionUuid, String userUuid);
	
	List<PrescriptionDto> getAllActivePrescriptionsAfter(Date date);
	
	List<PrescriptionDto> getByUuids(List<String> uuids);
	
	List<String> getAllActiveUuids(String userUuid);
	
	List<PrescriptionExportDto> getExportList(CaseCriteria caseCriteria, int first, int max);

}
