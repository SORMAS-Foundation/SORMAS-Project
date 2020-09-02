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
}
