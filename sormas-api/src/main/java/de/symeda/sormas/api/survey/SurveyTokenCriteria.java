/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2025 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.survey;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class SurveyTokenCriteria extends BaseCriteria {

	private static final long serialVersionUID = 4551275234176171493L;

	private SurveyReferenceDto survey;
	private String tokenLike;
	private CaseReferenceDto caseAssignedTo;

	public SurveyReferenceDto getSurvey() {
		return survey;
	}

	public void setSurvey(SurveyReferenceDto survey) {
		this.survey = survey;
	}

	public SurveyTokenCriteria survey(SurveyReferenceDto survey) {
		setSurvey(survey);
		return this;
	}

	public String getTokenLike() {
		return tokenLike;
	}

	public void setTokenLike(String tokenLike) {
		this.tokenLike = tokenLike;
	}

	public SurveyTokenCriteria tokenLike(String tokenLike) {
		setTokenLike(tokenLike);
		return this;
	}

	public CaseReferenceDto getCaseAssignedTo() {
		return caseAssignedTo;
	}

	public void setCaseAssignedTo(CaseReferenceDto caseAssignedTo) {
		this.caseAssignedTo = caseAssignedTo;
	}

	public SurveyTokenCriteria caseAssignedTo(CaseReferenceDto caseAssignedTo) {
		setCaseAssignedTo(caseAssignedTo);
		return this;
	}
}
