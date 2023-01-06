package de.symeda.sormas.api.clinicalcourse;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import io.swagger.v3.oas.annotations.media.Schema;

@DependingOnFeatureType(featureType = FeatureType.CLINICAL_MANAGEMENT)
@Schema(description = "Corresponding clinical course")
public class ClinicalCourseDto extends EntityDto {

	public static final String I18N_PREFIX = "ClinicalCourse";
	private static final long serialVersionUID = -2664896907352864261L;

	public static ClinicalCourseDto build() {
		ClinicalCourseDto clinicalCourse = new ClinicalCourseDto();
		clinicalCourse.setUuid(DataHelper.createUuid());
		return clinicalCourse;
	}

	public ClinicalCourseReferenceDto toReference() {
		return new ClinicalCourseReferenceDto(getUuid());
	}

}
