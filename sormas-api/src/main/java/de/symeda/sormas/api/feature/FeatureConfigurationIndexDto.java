/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.feature;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_UUID_MAX;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_UUID_MIN;

import java.util.Date;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.uuid.AbstractUuidDto;

public class FeatureConfigurationIndexDto extends AbstractUuidDto {

	private static final long serialVersionUID = -8033830301352311580L;

	public static final String I18N_PREFIX = "FeatureConfiguration";
	public static final String REGION_NAME = "regionName";
	public static final String DISTRICT_NAME = "districtName";
	public static final String ENABLED = "enabled";
	public static final String END_DATE = "endDate";

	@Pattern(regexp = UUID_REGEX, message = Validations.patternNotMatching)
	@Size(min = CHARACTER_LIMIT_UUID_MIN, max = CHARACTER_LIMIT_UUID_MAX, message = Validations.textSizeNotInRange)
	private String regionUuid;
	private String regionName;
	@Pattern(regexp = UUID_REGEX, message = Validations.patternNotMatching)
	@Size(min = CHARACTER_LIMIT_UUID_MIN, max = CHARACTER_LIMIT_UUID_MAX, message = Validations.textSizeNotInRange)
	private String districtUuid;
	private String districtName;
	private Disease disease;
	private boolean enabled;
	private Date endDate;

	public FeatureConfigurationIndexDto(
		String uuid,
		String regionUuid,
		String regionName,
		String districtUuid,
		String districtName,
		Disease disease,
		Boolean enabled,
		Date endDate) {

		super(uuid);
		this.regionUuid = regionUuid;
		this.regionName = regionName;
		this.districtUuid = districtUuid;
		this.districtName = districtName;
		this.disease = disease;
		this.enabled = enabled != null ? enabled : false;
		this.endDate = endDate;
	}

	public String getRegionUuid() {
		return regionUuid;
	}

	public void setRegionUuid(String regionUuid) {
		this.regionUuid = regionUuid;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getDistrictUuid() {
		return districtUuid;
	}

	public void setDistrictUuid(String districtUuid) {
		this.districtUuid = districtUuid;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
