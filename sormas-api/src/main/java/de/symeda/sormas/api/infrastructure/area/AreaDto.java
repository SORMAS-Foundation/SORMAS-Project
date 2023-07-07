package de.symeda.sormas.api.infrastructure.area;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.InfrastructureDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.FieldConstraints;

@DependingOnFeatureType(featureType = FeatureType.INFRASTRUCTURE_TYPE_AREA)
public class AreaDto extends InfrastructureDto {

	public static final String I18N_PREFIX = "Area";
	public static final String NAME = "name";
	public static final String EXTERNAL_ID = "externalId";

	private static final long serialVersionUID = -6241927331721175673L;

	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String name;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String externalId;

	public static AreaDto build() {
		AreaDto area = new AreaDto();
		area.setUuid(DataHelper.createUuid());
		return area;
	}

	public AreaReferenceDto toReference() {
		return new AreaReferenceDto(getUuid());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	@Override
	public String buildCaption() {
		return getName();
	}

	@JsonIgnore
	public String i18nPrefix() {
		return I18N_PREFIX;
	}
}
