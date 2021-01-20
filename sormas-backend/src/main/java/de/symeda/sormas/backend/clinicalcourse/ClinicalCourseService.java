package de.symeda.sormas.backend.clinicalcourse;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.backend.common.BaseAdoService;

@Stateless
@LocalBean
public class ClinicalCourseService extends BaseAdoService<ClinicalCourse> {

	public ClinicalCourseService() {
		super(ClinicalCourse.class);
	}

}
