package de.symeda.sormas.api.clinicalcourse;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.EmbeddedSensitiveData;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

@DependingOnFeatureType(featureType = FeatureType.CLINICAL_MANAGEMENT)
public class ClinicalVisitDto extends PseudonymizableDto {

	private static final long serialVersionUID = -8220449896773019721L;

	public static final long APPROXIMATE_JSON_SIZE_IN_BYTES = 7613;

	public static final String I18N_PREFIX = "ClinicalVisit";

	public static final String CLINICAL_COURSE = "clinicalCourse";
	public static final String SYMPTOMS = "symptoms";
	public static final String DISEASE = "disease";
	public static final String VISIT_DATE_TIME = "visitDateTime";
	public static final String VISIT_REMARKS = "visitRemarks";
	public static final String VISITING_PERSON = "visitingPerson";

	private ClinicalCourseReferenceDto clinicalCourse;
	@Valid
	@EmbeddedSensitiveData
	private SymptomsDto symptoms;
	private Disease disease;
	private Date visitDateTime;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String visitingPerson;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String visitRemarks;

	public static ClinicalVisitDto build(ClinicalCourseReferenceDto clinicalCourse, Disease disease) {

		ClinicalVisitDto clinicalVisit = new ClinicalVisitDto();
		clinicalVisit.setUuid(DataHelper.createUuid());
		clinicalVisit.setClinicalCourse(clinicalCourse);
		clinicalVisit.setSymptoms(SymptomsDto.build());
		clinicalVisit.setDisease(disease);
		clinicalVisit.setVisitDateTime(new Date());
		return clinicalVisit;
	}

	public ClinicalCourseReferenceDto getClinicalCourse() {
		return clinicalCourse;
	}

	public void setClinicalCourse(ClinicalCourseReferenceDto clinicalCourse) {
		this.clinicalCourse = clinicalCourse;
	}

	public SymptomsDto getSymptoms() {
		return symptoms;
	}

	public void setSymptoms(SymptomsDto symptoms) {
		this.symptoms = symptoms;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public Date getVisitDateTime() {
		return visitDateTime;
	}

	public void setVisitDateTime(Date visitDateTime) {
		this.visitDateTime = visitDateTime;
	}

	public String getVisitRemarks() {
		return visitRemarks;
	}

	public void setVisitRemarks(String visitRemarks) {
		this.visitRemarks = visitRemarks;
	}

	public String getVisitingPerson() {
		return visitingPerson;
	}

	public void setVisitingPerson(String visitingPerson) {
		this.visitingPerson = visitingPerson;
	}
}
