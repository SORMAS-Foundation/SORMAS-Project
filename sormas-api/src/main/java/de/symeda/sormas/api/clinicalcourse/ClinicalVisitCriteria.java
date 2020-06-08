package de.symeda.sormas.api.clinicalcourse;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.utils.IgnoreForUrl;

public class ClinicalVisitCriteria extends BaseCriteria implements Cloneable {

	private static final long serialVersionUID = 3366677994421979459L;

	private ClinicalCourseReferenceDto clinicalCourse;

	@Override
	public ClinicalVisitCriteria clone() {

		try {
			return (ClinicalVisitCriteria) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@IgnoreForUrl
	public ClinicalCourseReferenceDto getClinicalCourse() {
		return clinicalCourse;
	}

	public ClinicalVisitCriteria clinicalCourse(ClinicalCourseReferenceDto clinicalCourse) {
		this.clinicalCourse = clinicalCourse;
		return this;
	}
}
