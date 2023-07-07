package de.symeda.sormas.api.infrastructure.country;

import java.util.Date;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.InfrastructureDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.FieldConstraints;

@DependingOnFeatureType(featureType = {
	FeatureType.CASE_SURVEILANCE,
	FeatureType.EVENT_SURVEILLANCE,
	FeatureType.AGGREGATE_REPORTING })
public class CountryDto extends InfrastructureDto {

	private static final long serialVersionUID = 8309822957203823162L;

	public static final String I18N_PREFIX = "Country";
	public static final String DEFAULT_NAME = "defaultName";
	public static final String EXTERNAL_ID = "externalId";
	public static final String ISO_CODE = "isoCode";
	public static final String UNO_CODE = "unoCode";
	public static final String SUBCONTINENT = "subcontinent";

	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String defaultName;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String externalId;
	@Size(min = 2, max = 3, message = Validations.textSizeNotInRange)
	private String isoCode;
	@Size(min = 1, max = 3, message = Validations.textSizeNotInRange)
	private String unoCode;
	private SubcontinentReferenceDto subcontinent;

	public CountryDto(
		Date creationDate,
		Date changeDate,
		String uuid,
		boolean archived,
		String defaultName,
		String externalId,
		String isoCode,
		String unoCode,
		String subcontinentUuid,
		String subcontinentName,
		String subcontinentExternalId) {

		super(creationDate, changeDate, uuid, archived);
		this.defaultName = defaultName;
		this.externalId = externalId;
		this.isoCode = isoCode;
		this.unoCode = unoCode;
		if (subcontinentUuid != null) {
			this.subcontinent = new SubcontinentReferenceDto(subcontinentUuid, subcontinentName, subcontinentExternalId);
		}
	}

	public CountryDto() {
		super();
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

	public void setExternalId(String externalID) {
		this.externalId = externalID;
	}

	public String getIsoCode() {
		return isoCode;
	}

	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}

	public String getUnoCode() {
		return unoCode;
	}

	public void setUnoCode(String unoCode) {
		this.unoCode = unoCode;
	}
	public SubcontinentReferenceDto getSubcontinent() {
		return subcontinent;
	}

	public void setSubcontinent(SubcontinentReferenceDto subcontinent) {
		this.subcontinent = subcontinent;
	}

	@Override
	public String buildCaption() {
		return getDefaultName();
	}

	@JsonIgnore
	public String i18nPrefix() {
		return I18N_PREFIX;
	}

	public static CountryDto build() {
		CountryDto dto = new CountryDto();
		dto.setUuid(DataHelper.createUuid());
		return dto;
	}
}
