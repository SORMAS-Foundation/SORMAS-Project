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

package de.symeda.sormas.backend.infrastructure.country;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import de.symeda.sormas.backend.infrastructure.InfrastructureAdo;
import de.symeda.sormas.backend.infrastructure.subcontinent.Subcontinent;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Country extends InfrastructureAdo {

	private static final long serialVersionUID = -6050390899060395940L;

	public static final String TABLE_NAME = "country";

	public static final String DEFAULT_NAME = "defaultName";
	public static final String EXTERNAL_ID = "externalId";
	public static final String ISO_CODE = "isoCode";
	public static final String ISO2_CODE = "iso2Code";
	public static final String ISO_NUMERIC_CODE = "isoNumericCode";
	public static final String UNO_CODE = "unoCode";
	public static final String SUBCONTINENT = "subcontinent";
	public static final String NUTS_CODE = "nutsCode";
	public static final String EPIPULSE_LOCATION_CODE = "epipulseLocationCode";

	private String defaultName;
	private String externalId;
	private String isoCode;
	private String iso2Code;
	private String isoNumericCode;
	private String unoCode;
	private Subcontinent subcontinent;
	private String nutsCode;
	private String epipulseLocationCode;

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

	/** ISO 3166-1 alpha-3 code, e.g. {@code "ALB"} for Albania, {@code "GRC"} for Greece. */
	public String getIsoCode() {
		return isoCode;
	}

	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}

	/**
	 * ISO 3166-1 alpha-2 code, e.g. {@code "AL"} for Albania, {@code "GR"} for Greece and {@code "GB"} for the United
	 * Kingdom; unlike the EU codes {@code "EL"} and {@code "UK"} in {@link #getEpipulseLocationCode()}.
	 */
	@Column(length = 2)
	public String getIso2Code() {
		return iso2Code;
	}

	public void setIso2Code(String iso2Code) {
		this.iso2Code = iso2Code;
	}

	/** ISO 3166-1 numeric code with three digits, e.g. {@code "008"} for Albania; the same number as {@link #getUnoCode()}. */
	@Column(length = 3)
	public String getIsoNumericCode() {
		return isoNumericCode;
	}

	public void setIsoNumericCode(String isoNumericCode) {
		this.isoNumericCode = isoNumericCode;
	}

	/** UN M49 code without leading zeros, e.g. {@code "8"} for Albania. */
	public String getUnoCode() {
		return unoCode;
	}

	public void setUnoCode(String unoCode) {
		this.unoCode = unoCode;
	}

	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	public Subcontinent getSubcontinent() {
		return subcontinent;
	}

	public void setSubcontinent(Subcontinent subcontinent) {
		this.subcontinent = subcontinent;
	}

	/** NUTS code of the country, e.g. {@code "AL"} for Albania, {@code "EL"} for Greece; unique. Not used by EpiPulse. */
	public String getNutsCode() {
		return nutsCode;
	}

	public void setNutsCode(String nutsCode) {
		this.nutsCode = nutsCode;
	}

	/**
	 * Location code of the country in the Location tab of the ECDC metadata {@code EpiPulse_CasesMetadata.xlsx}, reported by
	 * the EpiPulse export, e.g. {@code "AL"} for Albania, {@code "EL"} for Greece and {@code "UK"} for the United Kingdom.
	 */
	@Column(length = 10)
	public String getEpipulseLocationCode() {
		return epipulseLocationCode;
	}

	public void setEpipulseLocationCode(String epipulseLocationCode) {
		this.epipulseLocationCode = epipulseLocationCode;
	}
}
