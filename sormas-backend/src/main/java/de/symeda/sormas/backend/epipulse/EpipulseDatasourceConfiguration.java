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

@Entity(name = "epipulse_datasource_configuration")
public class EpipulseDatasourceConfiguration extends AbstractDomainObject {

	public static final String TABLE_NAME = "epipulse_datasource_configuration";

	private String countryIso2Code;
	private String datasource;
	private String name;
	private String description;
	private String subjectcode;
	private Integer geographicalcoverage;
	private String outermostregions;
	private Date surveillancestartdate;
	private Date surveillanceenddate;
	private Date validfrom;
	private Date validto;

	@Column(name = "country_iso2_code", nullable = false, length = 2)
	public String getCountryIso2Code() {
		return countryIso2Code;
	}

	public void setCountryIso2Code(String countryIso2Code) {
		this.countryIso2Code = countryIso2Code;
	}

	@Column(name = "datasource", nullable = false)
	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	@Column(name = "name", nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "description", nullable = false)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "subjectcode", nullable = false)
	public String getSubjectcode() {
		return subjectcode;
	}

	public void setSubjectcode(String subjectcode) {
		this.subjectcode = subjectcode;
	}

	@Column(name = "geographicalcoverage")
	public Integer getGeographicalcoverage() {
		return geographicalcoverage;
	}

	public void setGeographicalcoverage(Integer geographicalcoverage) {
		this.geographicalcoverage = geographicalcoverage;
	}

	@Column(name = "outermostregions")
	public String getOutermostregions() {
		return outermostregions;
	}

	public void setOutermostregions(String outermostregions) {
		this.outermostregions = outermostregions;
	}

	@Column(name = "surveillancestartdate")
	public Date getSurveillancestartdate() {
		return surveillancestartdate;
	}

	public void setSurveillancestartdate(Date surveillancestartdate) {
		this.surveillancestartdate = surveillancestartdate;
	}

	@Column(name = "surveillanceenddate")
	public Date getSurveillanceenddate() {
		return surveillanceenddate;
	}

	public void setSurveillanceenddate(Date surveillanceenddate) {
		this.surveillanceenddate = surveillanceenddate;
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
