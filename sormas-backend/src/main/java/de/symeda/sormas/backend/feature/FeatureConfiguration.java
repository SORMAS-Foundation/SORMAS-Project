package de.symeda.sormas.backend.feature;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.util.ModelConstants;

@Entity(name = FeatureConfiguration.TABLE_NAME)
@Audited
public class FeatureConfiguration extends AbstractDomainObject {

	private static final long serialVersionUID = 4027927530101427321L;

	public static final String TABLE_NAME = "featureconfiguration";

	public static final String FEATURE_TYPE = "featureType";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String DISEASE = "disease";
	public static final String END_DATE = "endDate";
	public static final String ENABLED = "enabled";
	public static final String PROPERTIES = "properties";

	private FeatureType featureType;
	private Region region;
	private District district;
	private Disease disease;
	private Date endDate;
	private boolean enabled;
	private Map<FeatureTypeProperty, Object> properties;

	public static FeatureConfiguration build(FeatureType featureType, boolean enabled) {

		FeatureConfiguration configuration = new FeatureConfiguration();
		configuration.setFeatureType(featureType);
		configuration.setEnabled(enabled);
		return configuration;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public FeatureType getFeatureType() {
		return featureType;
	}

	public void setFeatureType(FeatureType featureType) {
		this.featureType = featureType;
	}

	@ManyToOne(cascade = {})
	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	@ManyToOne(cascade = {})
	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	@Enumerated(EnumType.STRING)
	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Column
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Type(type = ModelConstants.HIBERNATE_TYPE_JSON)
	@Column(columnDefinition = ModelConstants.COLUMN_DEFINITION_JSON)
	public Map<FeatureTypeProperty, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<FeatureTypeProperty, Object> properties) {
		this.properties = properties;
	}
}
