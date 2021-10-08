package de.symeda.sormas.api.disease;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;

public class DiseaseConfigurationDto extends EntityDto {

	private static final long serialVersionUID = -7653585175036656526L;

	private Disease disease;
	private Boolean active;
	private Boolean primaryDisease;
	private Boolean caseBased;
	private Boolean followUpEnabled;
	private Integer followUpDuration;
	private Integer caseFollowUpDuration;
	private Integer eventParticipantFollowUpDuration;
	private Boolean extendedClassification;
	private Boolean extendedClassificationMulti;

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

	public Boolean getCaseBased() {
		return caseBased;
	}

	public void setCaseBased(Boolean caseBased) {
		this.caseBased = caseBased;
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
}
