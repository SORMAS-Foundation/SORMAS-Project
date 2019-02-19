package de.symeda.sormas.api.clinicalcourse;

import java.util.List;

import javax.ejb.Remote;

@Remote
public interface ClinicalCourseFacade {

	List<ClinicalVisitIndexDto> getClinicalVisitIndexList(ClinicalVisitCriteria criteria);
	
	ClinicalVisitDto getClinicalVisitByUuid(String uuid);

	ClinicalVisitDto saveClinicalVisit(ClinicalVisitDto clinicalVisit, String caseUuid);
	
	void deleteClinicalVisit(String clinicalVisitUuid, String userUuid);
	
	ClinicalCourseDto saveClinicalCourse(ClinicalCourseDto clinicalCourse);

}
