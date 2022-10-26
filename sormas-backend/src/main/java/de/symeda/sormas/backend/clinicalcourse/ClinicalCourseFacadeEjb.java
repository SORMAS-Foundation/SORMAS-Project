package de.symeda.sormas.backend.clinicalcourse;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.clinicalcourse.ClinicalCourseDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalCourseFacade;
import de.symeda.sormas.api.clinicalcourse.ClinicalCourseReferenceDto;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "ClinicalCourseFacade")
public class ClinicalCourseFacadeEjb implements ClinicalCourseFacade {

	public static ClinicalCourseReferenceDto toReferenceDto(ClinicalCourse entity) {

		if (entity == null) {
			return null;
		}

		ClinicalCourseReferenceDto dto = new ClinicalCourseReferenceDto(entity.getUuid(), entity.toString());
		return dto;
	}

	public static ClinicalCourseDto toDto(ClinicalCourse source) {

		if (source == null) {
			return null;
		}

		ClinicalCourseDto target = new ClinicalCourseDto();
		DtoHelper.fillDto(target, source);

		return target;
	}

	public ClinicalCourse fillOrBuildEntity(@NotNull ClinicalCourseDto source, ClinicalCourse entity, boolean checkChangeDate) {
		return DtoHelper.fillOrBuildEntity(source, entity, ClinicalCourse::new, checkChangeDate);
	}

	@LocalBean
	@Stateless
	public static class ClinicalCourseFacadeEjbLocal extends ClinicalCourseFacadeEjb {

	}
}
