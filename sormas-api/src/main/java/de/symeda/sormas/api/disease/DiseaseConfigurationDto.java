package de.symeda.sormas.api.disease;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data transfer object for disease configuration related data")
public class DiseaseConfigurationDto extends EntityDto {

	private static final long serialVersionUID = -7653585175036656526L;

	private Disease disease;
	@Schema(description = "Whether research of the disease is activated on the server")
	private Boolean active;
	@Schema(description = "Whether the disease is set as primarily researched disease")
	private Boolean primaryDisease;
	@Schema(description = "Whether case surveillance is enabled for the disease")
	private Boolean caseSurveillanceEnabled;
	@Schema(description = "Whether aggregation of reports is activated for the disease")
	private Boolean aggregateReportingEnabled;
	@Schema(description = "Whether follow up is enabled for the disease")
	private Boolean followUpEnabled;
	@Schema(description = "Time duration in which a follow-up has to happen for people who had contact with the disease")
	private Integer followUpDuration;
	@Schema(description = "Time duration in which a follow-up has to happen for cases of the disease")
	private Integer caseFollowUpDuration;
	@Schema(description = "Time duration in which a follow-up has to happen for people that attended a event where a case was present")
	private Integer eventParticipantFollowUpDuration;
	@Schema(description = "Whether extended classification is activated for the disease")
	private Boolean extendedClassification;
	@Schema(description = "TBD_RESTAPI_SWAGGER_DOC")
	private Boolean extendedClassificationMulti;
	@Schema(description = "Separated age groups for data recorded about the disease")
	private List<String> ageGroups;

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getPrimaryDisease() {
		return primaryDisease;
	}

	public void setPrimaryDisease(Boolean primaryDisease) {
		this.primaryDisease = primaryDisease;
	}

	public Boolean getCaseSurveillanceEnabled() {
		return caseSurveillanceEnabled;
	}

	public void setCaseSurveillanceEnabled(Boolean caseSurveillanceEnabled) {
		this.caseSurveillanceEnabled = caseSurveillanceEnabled;
	}

	public Boolean getAggregateReportingEnabled() {
		return aggregateReportingEnabled;
	}

	public void setAggregateReportingEnabled(Boolean aggregateReportingEnabled) {
		this.aggregateReportingEnabled = aggregateReportingEnabled;
	}

	public Boolean getFollowUpEnabled() {
		return followUpEnabled;
	}

	public void setFollowUpEnabled(Boolean followUpEnabled) {
		this.followUpEnabled = followUpEnabled;
	}

	public Integer getFollowUpDuration() {
		return followUpDuration;
	}

	public void setFollowUpDuration(Integer followUpDuration) {
		this.followUpDuration = followUpDuration;
	}

	public Integer getCaseFollowUpDuration() {
		return caseFollowUpDuration;
	}

	public void setCaseFollowUpDuration(Integer caseFollowUpDuration) {
		this.caseFollowUpDuration = caseFollowUpDuration;
	}

	public Integer getEventParticipantFollowUpDuration() {
		return eventParticipantFollowUpDuration;
	}

	public void setEventParticipantFollowUpDuration(Integer eventParticipantFollowUpDuration) {
		this.eventParticipantFollowUpDuration = eventParticipantFollowUpDuration;
	}

	public Boolean getExtendedClassification() {
		return extendedClassification;
	}

	public void setExtendedClassification(Boolean extendedClassification) {
		this.extendedClassification = extendedClassification;
	}

	public Boolean getExtendedClassificationMulti() {
		return extendedClassificationMulti;
	}

	public void setExtendedClassificationMulti(Boolean extendedClassificationMulti) {
		this.extendedClassificationMulti = extendedClassificationMulti;
	}

	public List<String> getAgeGroups() {
		return ageGroups;
	}

	public void setAgeGroups(List<String> ageGroups) {
		this.ageGroups = ageGroups;
	}
}
