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

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.survey.SurveyTokenCriteria;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;

@Stateless
@LocalBean
public class SurveyTokenService extends BaseAdoService<SurveyToken> {

	public SurveyTokenService() {
		super(SurveyToken.class);
	}

	public Predicate buildCriteriaFilter(SurveyTokenCriteria criteria, CriteriaBuilder cb, Root<SurveyToken> root, SurveyTokenJoins joins) {
		Predicate filter = null;
		if (criteria.getSurvey() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getSurvey().get(Survey.UUID), criteria.getSurvey().getUuid()));
		}

		if (criteria.getTokenLike() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, CriteriaBuilderHelper.ilike(cb, root.get(SurveyToken.TOKEN), criteria.getTokenLike()));
		}

		return filter;
	}
}
