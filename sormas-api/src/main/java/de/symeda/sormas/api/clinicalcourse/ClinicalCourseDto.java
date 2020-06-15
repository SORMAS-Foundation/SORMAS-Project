package de.symeda.sormas.api.clinicalcourse;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.utils.DataHelper;

public class ClinicalCourseDto extends EntityDto {

	private static final long serialVersionUID = -2664896907352864261L;

	public static final String I18N_PREFIX = "ClinicalCourse";

	public static final String HEALTH_CONDITIONS = "healthConditions";

	private HealthConditionsDto healthConditions;

	public static ClinicalCourseDto build() {

		ClinicalCourseDto clinicalCourse = new ClinicalCourseDto();
		clinicalCourse.setUuid(DataHelper.createUuid());
		clinicalCourse.setHealthConditions(HealthConditionsDto.build());
		return clinicalCourse;
	}

	public ClinicalCourseReferenceDto toReference() {
		return new ClinicalCourseReferenceDto(getUuid());
	}

	public HealthConditionsDto getHealthConditions() {
		return healthConditions;
	}

	public void setHealthConditions(HealthConditionsDto healthConditions) {
		this.healthConditions = healthConditions;
	}
}
