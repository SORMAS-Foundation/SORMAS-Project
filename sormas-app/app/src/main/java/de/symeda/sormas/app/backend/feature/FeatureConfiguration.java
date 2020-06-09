package de.symeda.sormas.app.backend.feature;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

@Entity(name = FeatureConfiguration.TABLE_NAME)
@DatabaseTable(tableName = FeatureConfiguration.TABLE_NAME)
public class FeatureConfiguration extends AbstractDomainObject {

	private static final long serialVersionUID = 4027927530101427321L;

	public static final String TABLE_NAME = "featureConfiguration";
	public static final String I18N_PREFIX = "FeatureConfiguration";

	public static final String FEATURE_TYPE = "featureType";
	public static final String DISEASE = "disease";
	public static final String ENABLED = "enabled";
	public static final String END_DATE = "endDate";

	@Enumerated(EnumType.STRING)
	private FeatureType featureType;

	@Enumerated(EnumType.STRING)
	private Disease disease;

	@DatabaseField
	private boolean enabled;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date endDate;

	public FeatureType getFeatureType() {
		return featureType;
	}

	public void setFeatureType(FeatureType featureType) {
		this.featureType = featureType;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}
