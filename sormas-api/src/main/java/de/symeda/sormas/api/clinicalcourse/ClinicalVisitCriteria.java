package de.symeda.sormas.api.clinicalcourse;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.utils.IgnoreForUrl;

public class ClinicalVisitCriteria extends BaseCriteria implements Cloneable {

	private static final long serialVersionUID = 3366677994421979459L;
	
	private ClinicalCourseDto clinicalCourse;

	@Override
	public ClinicalVisitCriteria clone() {
		try {
			return (ClinicalVisitCriteria) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}	
	
	@IgnoreForUrl
	public ClinicalCourseDto getClinicalCourse() {
		return clinicalCourse;
	}
	
	public ClinicalVisitCriteria clinicalCourse(ClinicalCourseDto clinicalCourse) {
		this.clinicalCourse = clinicalCourse;
		return this;
	}
	
}
