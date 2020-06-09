/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.caze;

import java.io.Serializable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.backend.region.Region;

public class CaseCriteria implements Serializable {

	private String textFilter;
	private InvestigationStatus investigationStatus;
	private Disease disease;
	private CaseClassification caseClassification;
	private CaseOutcome outcome;
	private EpiWeek epiWeekFrom;
	private EpiWeek epiWeekTo;
	private CaseOrigin caseOrigin;
	private Region region;

	public CaseCriteria setTextFilter(String textFilter) {
		this.textFilter = textFilter;
		return this;
	}

	public String getTextFilter() {
		return textFilter;
	}

	public CaseCriteria setOutcome(CaseOutcome outcome) {
		this.outcome = outcome;
		return this;
	}

	public CaseOutcome getOutcome() {
		return outcome;
	}

	public CaseCriteria setDisease(Disease disease) {
		this.disease = disease;
		return this;
	}

	public Disease getDisease() {
		return disease;
	}

	public CaseCriteria setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
		return this;
	}

	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	public CaseCriteria setInvestigationStatus(InvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
		return this;
	}

	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public EpiWeek getEpiWeekFrom() {
		return epiWeekFrom;
	}

	public CaseCriteria setEpiWeekFrom(EpiWeek epiWeekFrom) {
		this.epiWeekFrom = epiWeekFrom;
		return this;
	}

	public EpiWeek getEpiWeekTo() {
		return epiWeekTo;
	}

	public CaseCriteria setEpiWeekTo(EpiWeek epiWeekTo) {
		this.epiWeekTo = epiWeekTo;
		return this;
	}

	public CaseOrigin getCaseOrigin() {
		return caseOrigin;
	}

	public CaseCriteria setCaseOrigin(CaseOrigin caseOrigin) {
		this.caseOrigin = caseOrigin;
		return this;
	}

	public Region getRegion() {
		return region;
	}

	public CaseCriteria setRegion(Region region) {
		this.region = region;
		return this;
	}
}
