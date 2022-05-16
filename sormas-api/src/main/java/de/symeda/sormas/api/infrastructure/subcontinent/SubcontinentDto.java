package de.symeda.sormas.api.infrastructure.subcontinent;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.InfrastructureDto;
import de.symeda.sormas.api.infrastructure.continent.ContinentReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.FieldConstraints;

@DependingOnFeatureType(featureType = {
	FeatureType.CASE_SURVEILANCE,
	FeatureType.EVENT_SURVEILLANCE,
	FeatureType.AGGREGATE_REPORTING })
public class SubcontinentDto extends InfrastructureDto {

	public static final String I18N_PREFIX = "Subcontinent";
	public static final String DEFAULT_NAME = "defaultName";
	public static final String EXTERNAL_ID = "externalId";
	public static final String CONTINENT = "continent";

	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String defaultName;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String externalId;
	private boolean archived;
	private ContinentReferenceDto continent;

	public static SubcontinentDto build() {
		SubcontinentDto dto = new SubcontinentDto();
		dto.setUuid(DataHelper.createUuid());
		return dto;
	}

	public String getDefaultName() {
		return defaultName;
	}

	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public ContinentReferenceDto getContinent() {
		return continent;
	}

	public void setContinent(ContinentReferenceDto continent) {
		this.continent = continent;
	}

	@Override
	public String toString() {
		return getDefaultName();
	}
}
