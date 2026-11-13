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

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentReferenceDto;

public class CountryIndexDto extends EntityDto {

	private static final long serialVersionUID = 8309822957203823162L;

	public static final String I18N_PREFIX = "Country";
	public static final String DEFAULT_NAME = "defaultName";
	public static final String DISPLAY_NAME = "displayName";
	public static final String EXTERNAL_ID = "externalId";
	public static final String ISO_CODE = "isoCode";
	public static final String ISO2_CODE = "iso2Code";
	public static final String ISO_NUMERIC_CODE = "isoNumericCode";
	public static final String UNO_CODE = "unoCode";
	public static final String SUBCONTINENT = "subcontinent";
	public static final String NUTS_CODE = "nutsCode";
	public static final String EPIPULSE_LOCATION_CODE = "epipulseLocationCode";

	private String defaultName;
	private String displayName;
	private String externalId;
	private String isoCode;
	/** ISO 3166-1 alpha-2 code, e.g. {@code "GR"} for Greece. */
	private String iso2Code;
	/** ISO 3166-1 numeric code with three digits, e.g. {@code "008"} for Albania. */
	private String isoNumericCode;
	private String unoCode;
	private boolean archived;
	private SubcontinentReferenceDto subcontinent;
	private String nutsCode;
	/** Location code of the country reported by the EpiPulse export, e.g. {@code "EL"} for Greece. */
	private String epipulseLocationCode;

	public String getDefaultName() {
		return defaultName;
	}

	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
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

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
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
}
