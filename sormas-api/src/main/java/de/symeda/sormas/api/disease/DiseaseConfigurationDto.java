package de.symeda.sormas.api.disease;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;

public class DiseaseConfigurationDto extends EntityDto {

	private static final long serialVersionUID = -7653585175036656526L;

	public static final String I18N_PREFIX = "DiseaseConfiguration";

	public static final String DISEASE = "disease";
	public static final String ACTIVE = "active";
	public static final String PRIMARY_DISEASE = "primaryDisease";
	public static final String CASE_SURVEILLANCE_ENABLED = "caseSurveillanceEnabled";
	public static final String AGGREGATE_REPORTING_ENABLED = "aggregateReportingEnabled";
	public static final String FOLLOW_UP_ENABLED = "followUpEnabled";
	public static final String FOLLOW_UP_DURATION = "followUpDuration";
	public static final String CASE_FOLLOW_UP_DURATION = "caseFollowUpDuration";
	public static final String EVENT_PARTICIPANT_FOLLOW_UP_DURATION = "eventParticipantFollowUpDuration";
	public static final String EXTENDED_CLASSIFICATION = "extendedClassification";
	public static final String EXTENDED_CLASSIFICATION_MULTI = "extendedClassificationMulti";
	public static final String AGE_GROUPS = "ageGroups";
	public static final String AUTOMATIC_SAMPLE_ASSIGNMENT_THRESHOLD = "automaticSampleAssignmentThreshold";

	private Disease disease;
	private Boolean active;
	private Boolean primaryDisease;
	private Boolean caseSurveillanceEnabled;
	private Boolean aggregateReportingEnabled;
	private Boolean followUpEnabled;
	private Integer followUpDuration;
	private Integer caseFollowUpDuration;
	private Integer eventParticipantFollowUpDuration;
	private Boolean extendedClassification;
	private Boolean extendedClassificationMulti;
	private List<String> ageGroups;
	private Integer automaticSampleAssignmentThreshold;

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

	public Integer getAutomaticSampleAssignmentThreshold() {
		return automaticSampleAssignmentThreshold;
	}

	public void setAutomaticSampleAssignmentThreshold(Integer automaticSampleAssignmentThreshold) {
		this.automaticSampleAssignmentThreshold = automaticSampleAssignmentThreshold;
	}
}
