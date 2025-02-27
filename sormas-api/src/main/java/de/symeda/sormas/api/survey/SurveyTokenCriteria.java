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
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class SurveyTokenCriteria extends BaseCriteria {

	private static final long serialVersionUID = 4551275234176171493L;

	public static final String FREE_TEXT = "freeText";
	public static final String RESPONSE_RECEIVED = "responseReceived";
	public static final String TOKEN_NOT_ASSIGNED = "tokenNotAssigned";

	private SurveyReferenceDto survey;
	private String freeText;
	private String token;
	private CaseReferenceDto caseAssignedTo;
	private Boolean responseReceived;
	private Boolean tokenNotAssigned;

	@IgnoreForUrl
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

	public String getFreeText() {
		return freeText;
	}

	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public SurveyTokenCriteria tokenLike(String tokenLike) {
		setFreeText(tokenLike);
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

	public Boolean getResponseReceived() {
		return responseReceived;
	}

	public void setResponseReceived(Boolean responseReceived) {
		this.responseReceived = responseReceived;
	}

	public Boolean getTokenNotAssigned() {
		return tokenNotAssigned;
	}

	public void setTokenNotAssigned(Boolean tokenNotAssigned) {
		this.tokenNotAssigned = tokenNotAssigned;
	}
}
