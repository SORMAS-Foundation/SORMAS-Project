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

package de.symeda.sormas.api.epipulse;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class EpipulseExportCriteria extends BaseCriteria implements Serializable {

	private static final long serialVersionUID = -1651654629133166528L;

	public static final String I18N_PREFIX = "EpipulseExport";

	public static final String SUBJECT_CODE = "subjectCode";
	public static final String REPORT_DATE_FROM = "reportDateFrom";
	public static final String REPORT_DATE_TO = "reportDateTo";
	public static final String STATUS = "status";
	public static final String RELEVANCE_STATUS = "relevanceStatus";

	private EpipulseSubjectCode subjectCode;
	private Date reportDateFrom;
	private Date reportDateTo;
	private EpipulseExportStatus status;
	private EntityRelevanceStatus relevanceStatus;

	public EpipulseSubjectCode getSubjectCode() {
		return subjectCode;
	}

	public void setSubjectCode(EpipulseSubjectCode subjectCode) {
		this.subjectCode = subjectCode;
	}

	public EpipulseExportCriteria disease() {
		setSubjectCode(subjectCode);
		return this;
	}

	public Date getReportDateFrom() {
		return reportDateFrom;
	}

	public void setReportDateFrom(Date reportDateFrom) {
		this.reportDateFrom = reportDateFrom;
	}

	public EpipulseExportCriteria reportDateFrom(Date reportDateFrom) {
		setReportDateFrom(reportDateFrom);
		return this;
	}

	public Date getReportDateTo() {
		return reportDateTo;
	}

	public void setReportDateTo(Date reportDateTo) {
		this.reportDateTo = reportDateTo;
	}

	public EpipulseExportCriteria reportDateTo(Date reportDateTo) {
		setReportDateTo(reportDateTo);
		return this;
	}

	public EpipulseExportStatus getStatus() {
		return status;
	}

	public void setStatus(EpipulseExportStatus status) {
		this.status = status;
	}

	public EpipulseExportCriteria status(EpipulseExportStatus status) {
		setStatus(status);
		return this;
	}

	public EntityRelevanceStatus getRelevanceStatus() {
		return relevanceStatus;
	}

	public void setRelevanceStatus(EntityRelevanceStatus relevanceStatus) {
		this.relevanceStatus = relevanceStatus;
	}

	public EpipulseExportCriteria relevanceStatus(EntityRelevanceStatus relevanceStatus) {
		setRelevanceStatus(relevanceStatus);
		return this;
	}
}
