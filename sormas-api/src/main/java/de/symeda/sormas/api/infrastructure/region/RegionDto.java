/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.api.infrastructure.region;

import java.util.Date;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.InfrastructureDtoWithDefault;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.FeatureIndependent;
import de.symeda.sormas.api.utils.FieldConstraints;
@AuditedClass
@FeatureIndependent
public class RegionDto extends InfrastructureDtoWithDefault {

	private static final long serialVersionUID = -1610675328037466348L;

	public static final String I18N_PREFIX = "Region";
	public static final String NAME = "name";
	public static final String EPID_CODE = "epidCode";
	public static final String GROWTH_RATE = "growthRate";
	public static final String EXTERNAL_ID = "externalID";
	public static final String AREA = "area";
	public static final String COUNTRY = "country";

	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String name;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String epidCode;
	private Float growthRate;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String externalID;
	@DependingOnFeatureType(featureType = FeatureType.INFRASTRUCTURE_TYPE_AREA)
	private AreaReferenceDto area;
	private CountryReferenceDto country;

	public RegionDto(
		Date creationDate,
		Date changeDate,
		String uuid,
		boolean archived,
		String name,
		String epidCode,
		Float growthRate,
		String externalID,
		String countryUuid,
		String countryName,
		String countryIsoCode,
		String areaUuid) {

		super(creationDate, changeDate, uuid, archived);
		this.name = name;
		this.epidCode = epidCode;
		this.growthRate = growthRate;
		this.externalID = externalID;

		if (countryUuid != null) {
			this.country = new CountryReferenceDto(countryUuid, I18nProperties.getCountryName(countryIsoCode, countryName), countryIsoCode);
		}

		if (areaUuid != null) {
			this.area = new AreaReferenceDto(areaUuid);
		}
	}

	public RegionDto() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEpidCode() {
		return epidCode;
	}

	public void setEpidCode(String epidCode) {
		this.epidCode = epidCode;
	}

	public Float getGrowthRate() {
		return growthRate;
	}

	public void setGrowthRate(Float growthRate) {
		this.growthRate = growthRate;
	}

	public String getExternalID() {
		return externalID;
	}

	public void setExternalID(String externalID) {
		this.externalID = externalID;
	}

	@DependingOnFeatureType(featureType = FeatureType.INFRASTRUCTURE_TYPE_AREA)
	public AreaReferenceDto getArea() {
		return area;
	}

	public void setArea(AreaReferenceDto area) {
		this.area = area;
	}

	public CountryReferenceDto getCountry() {
		return country;
	}

	public void setCountry(CountryReferenceDto country) {
		this.country = country;
	}

	public RegionReferenceDto toReference() {
		return new RegionReferenceDto(getUuid(), name, externalID);
	}

	public static RegionDto build() {
		RegionDto dto = new RegionDto();
		dto.setUuid(DataHelper.createUuid());
		return dto;
	}

	@Override
	public String buildCaption() {
		return getName();
	}

	@JsonIgnore
	public String i18nPrefix() {
		return I18N_PREFIX;
	}
	@Override
	public String toString() {
		return getName();
	}
}
