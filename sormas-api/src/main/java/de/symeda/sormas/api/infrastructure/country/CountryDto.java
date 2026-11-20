/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
	public static final String ISO2_CODE = "iso2Code";
	public static final String ISO_NUMERIC_CODE = "isoNumericCode";
	public static final String UNO_CODE = "unoCode";
	public static final String SUBCONTINENT = "subcontinent";
	public static final String NUTS_CODE = "nutsCode";
	public static final String EPIPULSE_LOCATION_CODE = "epipulseLocationCode";

	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String defaultName;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String externalId;
	/** ISO 3166-1 alpha-3 code, e.g. {@code "ALB"}. */
	@Size(min = 2, max = 3, message = Validations.textSizeNotInRange)
	private String isoCode;
	/** ISO 3166-1 alpha-2 code, e.g. {@code "AL"}; {@code "GR"} for Greece, {@code "GB"} for the United Kingdom. */
	@Size(min = 2, max = 2, message = Validations.textSizeNotInRange)
	private String iso2Code;
	/** ISO 3166-1 numeric code with three digits, e.g. {@code "008"}; the same number as {@link #unoCode}. */
	@Size(min = 3, max = 3, message = Validations.textSizeNotInRange)
	private String isoNumericCode;
	/** UN M49 code without leading zeros, e.g. {@code "8"}. */
	@Size(min = 1, max = 3, message = Validations.textSizeNotInRange)
	private String unoCode;
	private SubcontinentReferenceDto subcontinent;
	/** NUTS code of the country, e.g. {@code "AL"}; {@code "EL"} for Greece. Not used by the EpiPulse export. */
	private String nutsCode;
	/**
	 * Location code of the country in the ECDC metadata, reported by the EpiPulse export, e.g. {@code "AL"}; {@code "EL"} for
	 * Greece, {@code "UK"} for the United Kingdom.
	 */
	@Size(max = 10, message = Validations.textTooLong)
	private String epipulseLocationCode;

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

	public String getIso2Code() {
		return iso2Code;
	}

	public void setIso2Code(String iso2Code) {
		this.iso2Code = iso2Code;
	}

	public String getIsoNumericCode() {
		return isoNumericCode;
	}

	public void setIsoNumericCode(String isoNumericCode) {
		this.isoNumericCode = isoNumericCode;
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

	public String getNutsCode() {
		return nutsCode;
	}

	public void setNutsCode(String nutsCode) {
		this.nutsCode = nutsCode;
	}

	public String getEpipulseLocationCode() {
		return epipulseLocationCode;
	}

	public void setEpipulseLocationCode(String epipulseLocationCode) {
		this.epipulseLocationCode = epipulseLocationCode;
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
