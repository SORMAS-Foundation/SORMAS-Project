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

package de.symeda.sormas.backend.survey;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseJoins;
import de.symeda.sormas.backend.common.QueryJoins;

public class SurveyTokenJoins extends QueryJoins<SurveyToken> {

	private Join<SurveyToken, Survey> survey;
	private Join<SurveyToken, Case> caseAssignedTo;
	private CaseJoins caseAssignedToJoins;

	public SurveyTokenJoins(From<?, SurveyToken> root) {
		super(root);
	}

	public Join<SurveyToken, Survey> getSurvey() {
		return getOrCreate(survey, SurveyToken.SURVEY, JoinType.LEFT, this::setSurvey);
	}

	private void setSurvey(Join<SurveyToken, Survey> survey) {
		this.survey = survey;
	}

	public Join<SurveyToken, Case> getCaseAssignedTo() {
		return getOrCreate(caseAssignedTo, SurveyToken.CASE_ASSIGNED_TO, JoinType.LEFT, this::setCaseAssignedTo);
	}

	private void setCaseAssignedTo(Join<SurveyToken, Case> caseAssignedTo) {
		this.caseAssignedTo = caseAssignedTo;
	}

	public CaseJoins getCaseAssignedToJoins() {
		return getOrCreate(caseAssignedToJoins, () -> new CaseJoins(getCaseAssignedTo()), this::setCaseAssignedToJoins);
	}

	private void setCaseAssignedToJoins(CaseJoins caseAssignedToJoins) {
		this.caseAssignedToJoins = caseAssignedToJoins;
	}
}
