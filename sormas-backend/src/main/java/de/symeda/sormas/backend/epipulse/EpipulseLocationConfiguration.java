/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.epipulse;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity(name = "epipulse_location_configuration")
public class EpipulseLocationConfiguration extends AbstractDomainObject {

	public static final String TABLE_NAME = "epipulse_location_configuration";

	private String type;
	private String code;
	private String name;
	private String euShortname;
	private String euFullname;
	private String administrativeCentre;
	private String countryIso2Code;
	private String countryIso3Code;
	private Date validfrom;
	private Date validto;

	@Column(name = "type", nullable = false)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "code", nullable = false)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "name", nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "eu_shortname")
	public String getEuShortname() {
		return euShortname;
	}

	public void setEuShortname(String euShortname) {
		this.euShortname = euShortname;
	}

	@Column(name = "eu_fullname")
	public String getEuFullname() {
		return euFullname;
	}

	public void setEuFullname(String euFullname) {
		this.euFullname = euFullname;
	}

	@Column(name = "administrative_centre")
	public String getAdministrativeCentre() {
		return administrativeCentre;
	}

	public void setAdministrativeCentre(String administrativeCentre) {
		this.administrativeCentre = administrativeCentre;
	}

	@Column(name = "country_iso2_code", nullable = false, length = 2)
	public String getCountryIso2Code() {
		return countryIso2Code;
	}

	public void setCountryIso2Code(String countryIso2Code) {
		this.countryIso2Code = countryIso2Code;
	}

	@Column(name = "country_iso3_code", length = 3)
	public String getCountryIso3Code() {
		return countryIso3Code;
	}

	public void setCountryIso3Code(String countryIso3Code) {
		this.countryIso3Code = countryIso3Code;
	}

	@Column(name = "validfrom")
	public Date getValidfrom() {
		return validfrom;
	}

	public void setValidfrom(Date validfrom) {
		this.validfrom = validfrom;
	}

	@Column(name = "validto")
	public Date getValidto() {
		return validto;
	}

	public void setValidto(Date validto) {
		this.validto = validto;
	}

}
