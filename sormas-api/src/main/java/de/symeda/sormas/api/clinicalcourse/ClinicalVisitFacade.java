package de.symeda.sormas.api.clinicalcourse;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.Remote;
import javax.validation.Valid;

import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface ClinicalVisitFacade {

	List<ClinicalVisitIndexDto> getIndexList(
		ClinicalVisitCriteria clinicalVisitCriteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties);

	Page<ClinicalVisitIndexDto> getIndexPage(
		ClinicalVisitCriteria clinicalVisitCriteria,
		Integer offset,
		Integer size,
		List<SortProperty> sortProperties);

	ClinicalVisitDto getClinicalVisitByUuid(String uuid);

	ClinicalVisitDto saveClinicalVisit(ClinicalVisitDto clinicalVisit, String caseUuid);

	ClinicalVisitDto saveClinicalVisit(@Valid ClinicalVisitDto clinicalVisit);

	long count(ClinicalVisitCriteria clinicalVisitCriteria);

	void deleteClinicalVisit(String clinicalVisitUuid);

	List<ClinicalVisitDto> getAllActiveClinicalVisitsAfter(Date date);

	List<ClinicalVisitDto> getAllActiveClinicalVisitsAfter(Date date, Integer batchSize, String lastSynchronizedUuid);

	List<ClinicalVisitDto> getByUuids(List<String> uuids);

	List<String> getAllActiveUuids();

	List<ClinicalVisitExportDto> getExportList(CaseCriteria criteria, Collection<String> selectedRows, int first, int max);
}
