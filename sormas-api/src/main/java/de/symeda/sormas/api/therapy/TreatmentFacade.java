package de.symeda.sormas.api.therapy;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.Remote;
import javax.validation.Valid;

import de.symeda.sormas.api.caze.CaseCriteria;

@Remote
public interface TreatmentFacade {

	List<TreatmentIndexDto> getIndexList(TreatmentCriteria criteria);

	List<TreatmentIndexDto> getTreatmentForPrescription(List<String> prescriptionUuids);

	TreatmentDto getTreatmentByUuid(String uuid);

	TreatmentDto saveTreatment(@Valid TreatmentDto treatment);

	void deleteTreatment(String treatmentUuid);

	void deleteTreatments(List<String> treatmentUuids);

	void unlinkPrescriptionFromTreatments(List<String> treatmentUuids);

	List<TreatmentDto> getAllActiveTreatmentsAfter(Date date);

	List<TreatmentDto> getAllActiveTreatmentsAfter(Date date, Integer batchSize, String lastSynchronizedUuid);

	List<TreatmentDto> getByUuids(List<String> uuids);

	List<String> getAllActiveUuids();

	List<TreatmentExportDto> getExportList(CaseCriteria criteria, Collection<String> selectedRows, int first, int max);
}
