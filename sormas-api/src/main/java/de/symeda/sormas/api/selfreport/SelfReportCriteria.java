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

package de.symeda.sormas.api.selfreport;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class SelfReportCriteria extends BaseCriteria {

	private static final long serialVersionUID = 7245463026500908524L;

	private SelfReportInvestigationStatus investigationStatus;
	private EntityRelevanceStatus relevanceStatus;

	public SelfReportInvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public void setInvestigationStatus(SelfReportInvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}

	public EntityRelevanceStatus getRelevanceStatus() {
		return relevanceStatus;
	}

	public void setRelevanceStatus(EntityRelevanceStatus relevanceStatus) {
		this.relevanceStatus = relevanceStatus;
	}

	public SelfReportCriteria relevanceStatus(EntityRelevanceStatus relevanceStatus) {
		setRelevanceStatus(relevanceStatus);
		return this;
	}
}
