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

package de.symeda.sormas.api.dashboard;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.audit.AuditInclude;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.sample.PathogenTestResultType;

@AuditedClass
public class PathogenTestResultDto implements Serializable {

	private static final long serialVersionUID = 1L;
	@AuditInclude
	private Long caseId;
	private PathogenTestResultType pathogenTestResultType;
	private Date sampleDateTime;

	public PathogenTestResultDto(Long caseId, PathogenTestResultType pathogenTestResultType, Date sampleDateTime) {
		this.caseId = caseId;
		this.pathogenTestResultType = pathogenTestResultType;
		this.sampleDateTime = sampleDateTime;
	}

	public Long getCaseId() {
		return caseId;
	}

	public void setCaseId(Long caseId) {
		this.caseId = caseId;
	}

	public PathogenTestResultType getPathogenTestResultType() {
		return pathogenTestResultType;
	}

	public void setPathogenTestResultType(PathogenTestResultType pathogenTestResultType) {
		this.pathogenTestResultType = pathogenTestResultType;
	}

	public Date getSampleDateTime() {
		return sampleDateTime;
	}

	public void setSampleDateTime(Date sampleDateTime) {
		this.sampleDateTime = sampleDateTime;
	}
}
