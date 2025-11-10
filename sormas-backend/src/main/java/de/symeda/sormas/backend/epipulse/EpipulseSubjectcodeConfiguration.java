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

@Entity(name = "epipulse_subjectcode_configuration")
public class EpipulseSubjectcodeConfiguration extends AbstractDomainObject {

	private String subjectcode;
	private String name;
	private String disease;
	private String diseasename;
	private String healthtopic;
	private String healthtopicname;
	private Boolean aggregatedreporting = false;
	private Date validfrom;
	private Date validto;

	@Column(name = "subjectcode", nullable = false)
	public String getSubjectcode() {
		return subjectcode;
	}

	public void setSubjectcode(String subjectcode) {
		this.subjectcode = subjectcode;
	}

	@Column(name = "name", nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "disease")
	public String getDisease() {
		return disease;
	}

	public void setDisease(String disease) {
		this.disease = disease;
	}

	@Column(name = "diseasename")
	public String getDiseasename() {
		return diseasename;
	}

	public void setDiseasename(String diseasename) {
		this.diseasename = diseasename;
	}

	@Column(name = "healthtopic")
	public String getHealthtopic() {
		return healthtopic;
	}

	public void setHealthtopic(String healthtopic) {
		this.healthtopic = healthtopic;
	}

	@Column(name = "healthtopicname")
	public String getHealthtopicname() {
		return healthtopicname;
	}

	public void setHealthtopicname(String healthtopicname) {
		this.healthtopicname = healthtopicname;
	}

	@Column(name = "aggregatedreporting", nullable = false)
	public Boolean getAggregatedreporting() {
		return aggregatedreporting;
	}

	public void setAggregatedreporting(Boolean aggregatedreporting) {
		this.aggregatedreporting = aggregatedreporting;
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
