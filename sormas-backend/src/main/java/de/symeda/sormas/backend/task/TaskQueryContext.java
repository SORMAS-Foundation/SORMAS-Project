/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.task;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;

import de.symeda.sormas.backend.common.QueryContext;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;

public class TaskQueryContext extends QueryContext<Task, TaskJoins> {

	protected TaskQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, Task> root) {
		super(cb, query, root, new TaskJoins(root));
	}

	@Override
	protected Expression<?> createExpression(String name) {
		return null;
	}

	public Expression<String> getRegionNameForIndex() {
		return getRegionExpressionForIndex(Region.NAME);
	}

	public Expression<String> getRegionExpressionForIndex(String regionProperty) {
		return getIndexJurisdictionExpression(
			criteriaBuilder,
			joins.getCaseResponsibleRegion(),
			joins.getCaseRegion(),
			joins.getContactRegion(),
			joins.getEventRegion(),
			joins.getTravelEntryResponsibleRegion(),
			joins.getEnvironmentRegion(),
			regionProperty);
	}

	public Expression<String> getDistrictNameForIndex() {
		return getDistrictExpressionForIndex(District.NAME);
	}

	public Expression<String> getDistrictExpressionForIndex(String districtProperty) {
		return getIndexJurisdictionExpression(
			criteriaBuilder,
			joins.getCaseResponsibleDistrict(),
			joins.getCaseDistrict(),
			joins.getContactDistrict(),
			joins.getEventDistrict(),
			joins.getTravelEntryResponsibleDistrict(),
			joins.getEnvironmentDistrict(),
			districtProperty);
	}

	public Expression<String> getCommunityExpressionForIndex(String communityProperty) {
		return getIndexJurisdictionExpression(
			criteriaBuilder,
			joins.getCaseResponsibleCommunity(),
			joins.getCaseCommunity(),
			joins.getContactCommunity(),
			joins.getEventCommunity(),
			joins.getTravelEntryResponsibleCommunity(),
			joins.getEnvironmentCommunity(),
			communityProperty);
	}

	private <T> Expression<T> getIndexJurisdictionExpression(
		CriteriaBuilder cb,
		Join<?, ?> caseResponsibleJurisdictionJoin,
		Join<?, ?> caseJurisdictionJoin,
		Join<?, ?> contactJurisdictionJoin,
		Join<?, ?> eventJurisdictionJoin,
		Join<?, ?> travelEntryResponsibleJurisdictionJoin,
		Join<?, ?> environmentJurisdictionJoin,
		String propertyName) {

		return cb.<T> selectCase()
			.when(cb.isNotNull(caseResponsibleJurisdictionJoin), caseResponsibleJurisdictionJoin.get(propertyName))
			.otherwise(
				cb.<T> selectCase()
					.when(cb.isNotNull(caseJurisdictionJoin), caseJurisdictionJoin.get(propertyName))
					.otherwise(
						cb.<T> selectCase()
							.when(cb.isNotNull(contactJurisdictionJoin), contactJurisdictionJoin.get(propertyName))
							.otherwise(
								cb.<T> selectCase()
									.when(cb.isNotNull(eventJurisdictionJoin), eventJurisdictionJoin.get(propertyName))
									.otherwise(
										cb.<T> selectCase()
											.when(
												cb.isNotNull(travelEntryResponsibleJurisdictionJoin),
												travelEntryResponsibleJurisdictionJoin.get(propertyName))
											.otherwise(environmentJurisdictionJoin.get(propertyName))))));
	}
}
