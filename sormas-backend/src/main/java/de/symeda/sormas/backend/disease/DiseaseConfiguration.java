package de.symeda.sormas.backend.disease;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_TEXT;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.audit.AuditIgnore;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity(name = DiseaseConfiguration.TABLE_NAME)
@AuditIgnore(retainWrites = true)
public class DiseaseConfiguration extends AbstractDomainObject {

	private static final long serialVersionUID = -7653585175036656526L;

	public static final String TABLE_NAME = "diseaseconfiguration";

	public static final String DISEASE = "disease";
	public static final String PRIMARY_DISEASE = "primaryDisease";
	public static final String CASE_SURVEILLANCE_ENABLED = "caseSurveillanceEnabled";

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

	public static DiseaseConfiguration build(Disease disease) {
		DiseaseConfiguration configuration = new DiseaseConfiguration();
		configuration.setDisease(disease);
		return configuration;
	}

	@Enumerated(EnumType.STRING)
	@Column(unique = true)
	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	@Column
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@Column
	public Boolean getPrimaryDisease() {
		return primaryDisease;
	}

	public void setPrimaryDisease(Boolean primaryDisease) {
		this.primaryDisease = primaryDisease;
	}

	@Column
	public Boolean getCaseSurveillanceEnabled() {
		return caseSurveillanceEnabled;
	}

	public void setCaseSurveillanceEnabled(Boolean caseBased) {
		this.caseSurveillanceEnabled = caseBased;
	}

	@Column
	public Boolean getAggregateReportingEnabled() {
		return aggregateReportingEnabled;
	}

	public void setAggregateReportingEnabled(Boolean aggregateReportingEnabled) {
		this.aggregateReportingEnabled = aggregateReportingEnabled;
	}

	@Column
	public Boolean getFollowUpEnabled() {
		return followUpEnabled;
	}

	public void setFollowUpEnabled(Boolean followUpEnabled) {
		this.followUpEnabled = followUpEnabled;
	}

	@Column
	public Integer getFollowUpDuration() {
		return followUpDuration;
	}

	public void setFollowUpDuration(Integer followUpDuration) {
		this.followUpDuration = followUpDuration;
	}

	@Column
	public Integer getCaseFollowUpDuration() {
		return caseFollowUpDuration;
	}

	public void setCaseFollowUpDuration(Integer caseFollowUpDuration) {
		this.caseFollowUpDuration = caseFollowUpDuration;
	}

	@Column
	public Integer getEventParticipantFollowUpDuration() {
		return eventParticipantFollowUpDuration;
	}

	public void setEventParticipantFollowUpDuration(Integer eventParticipantFollowUpDuration) {
		this.eventParticipantFollowUpDuration = eventParticipantFollowUpDuration;
	}

	@Column
	public Boolean getExtendedClassification() {
		return extendedClassification;
	}

	public void setExtendedClassification(Boolean extendedClassification) {
		this.extendedClassification = extendedClassification;
	}

	@Column
	public Boolean getExtendedClassificationMulti() {
		return extendedClassificationMulti;
	}

	public void setExtendedClassificationMulti(Boolean extendedClassificationMulti) {
		this.extendedClassificationMulti = extendedClassificationMulti;
	}

	@Column(length = CHARACTER_LIMIT_TEXT)
	@Convert(converter = AgeGroupsConverter.class)
	public List<String> getAgeGroups() {
		return ageGroups;
	}

	public void setAgeGroups(List<String> ageGroups) {
		this.ageGroups = ageGroups;
	}

	@Column
	public Integer getAutomaticSampleAssignmentThreshold() {
		return automaticSampleAssignmentThreshold;
	}

	public void setAutomaticSampleAssignmentThreshold(Integer automaticSampleAssignmentThreshold) {
		this.automaticSampleAssignmentThreshold = automaticSampleAssignmentThreshold;
	}
}
