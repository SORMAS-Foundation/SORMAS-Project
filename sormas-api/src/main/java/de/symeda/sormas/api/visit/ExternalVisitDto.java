package de.symeda.sormas.api.visit;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_UUID_MAX;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_UUID_MIN;
import static de.symeda.sormas.api.uuid.HasUuid.UUID_REGEX;

import java.io.Serializable;
import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.audit.AuditIncludeProperty;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.SensitiveData;

/**
 * The class ExternalVisitDto.
 */
@AuditedClass
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExternalVisitDto implements Serializable, Cloneable {

	private static final long serialVersionUID = 7909093498222091926L;

	@NotBlank(message = Validations.requiredField)
	@Pattern(regexp = UUID_REGEX, message = Validations.patternNotMatching)
	@Size(min = CHARACTER_LIMIT_UUID_MIN, max = CHARACTER_LIMIT_UUID_MAX, message = Validations.textSizeNotInRange)
	@AuditIncludeProperty
	private String personUuid;
	@NotNull(message = Validations.validDisease)
	private Disease disease;
	@NotNull(message = Validations.visitDate)
	@AuditIncludeProperty
	private Date visitDateTime;
	@NotNull(message = Validations.visitStatus)
	private VisitStatus visitStatus;
	@SensitiveData
	@Size(max = CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String visitRemarks;

	@Valid
	private SymptomsDto symptoms;

	@Min(value = -90, message = Validations.numberTooSmall)
	@Max(value = 90, message = Validations.numberTooBig)
	private Double reportLat;
	@Min(value = -180, message = Validations.numberTooSmall)
	@Max(value = 180, message = Validations.numberTooBig)
	private Double reportLon;
	private Float reportLatLonAccuracy;

	public static ExternalVisitDto build(
		String personUuid,
		Disease disease,
		Date visitDateTime,
		VisitStatus visitStatus,
		String visitRemarks,
		SymptomsDto symptoms,
		Double reportLat,
		Double reportLon,
		Float reportLatLonAccuracy) {
		final ExternalVisitDto externalVisitDto = new ExternalVisitDto();
		externalVisitDto.setPersonUuid(personUuid);
		externalVisitDto.setDisease(disease);
		externalVisitDto.setVisitDateTime(visitDateTime);
		externalVisitDto.setVisitStatus(visitStatus);
		externalVisitDto.setVisitRemarks(visitRemarks);
		externalVisitDto.setSymptoms(symptoms);
		externalVisitDto.setReportLat(reportLat);
		externalVisitDto.setReportLon(reportLon);
		externalVisitDto.setReportLatLonAccuracy(reportLatLonAccuracy);
		return externalVisitDto;
	}

	public Date getVisitDateTime() {
		return visitDateTime;
	}

	public void setVisitDateTime(Date visitDateTime) {
		this.visitDateTime = visitDateTime;
	}

	public VisitStatus getVisitStatus() {
		return visitStatus;
	}

	public void setVisitStatus(VisitStatus visitStatus) {
		this.visitStatus = visitStatus;
	}

	public String getVisitRemarks() {
		return visitRemarks;
	}

	public void setVisitRemarks(String visitRemarks) {
		this.visitRemarks = visitRemarks;
	}

	public SymptomsDto getSymptoms() {
		return symptoms;
	}

	public void setSymptoms(SymptomsDto symptoms) {
		this.symptoms = symptoms;
	}

	public Double getReportLat() {
		return reportLat;
	}

	public void setReportLat(Double reportLat) {
		this.reportLat = reportLat;
	}

	public Double getReportLon() {
		return reportLon;
	}

	public void setReportLon(Double reportLon) {
		this.reportLon = reportLon;
	}

	public Float getReportLatLonAccuracy() {
		return reportLatLonAccuracy;
	}

	public void setReportLatLonAccuracy(Float reportLatLonAccuracy) {
		this.reportLatLonAccuracy = reportLatLonAccuracy;
	}

	public String getPersonUuid() {
		return personUuid;
	}

	public void setPersonUuid(String personUuid) {
		this.personUuid = personUuid;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}
}
