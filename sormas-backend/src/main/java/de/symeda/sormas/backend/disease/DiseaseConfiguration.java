package de.symeda.sormas.backend.disease;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity(name = DiseaseConfiguration.TABLE_NAME)
@Audited
public class DiseaseConfiguration extends AbstractDomainObject {

	private static final long serialVersionUID = -7653585175036656526L;

	public static final String TABLE_NAME = "diseaseconfiguration";

	public static final String DISEASE = "disease";

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
		if (disease != Disease.CORONAVIRUS && disease != Disease.MEASLES) {
			this.extendedClassification = false;
		} else {
			this.extendedClassification = true;
		}

		if (disease != Disease.CORONAVIRUS) {
			this.extendedClassificationMulti = false;
		} else {
			this.extendedClassificationMulti = true;
		}
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
	public Boolean getCaseBased() {
		return caseBased;
	}

	public void setCaseBased(Boolean caseBased) {
		this.caseBased = caseBased;
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
}
