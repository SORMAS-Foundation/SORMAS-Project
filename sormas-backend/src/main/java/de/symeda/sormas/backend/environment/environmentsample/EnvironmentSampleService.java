/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.environment.environmentsample;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleCriteria;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.AbstractDeletableAdoService;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.environment.Environment;
import de.symeda.sormas.backend.environment.EnvironmentService;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.user.UserService;

@Stateless
@LocalBean
public class EnvironmentSampleService extends AbstractDeletableAdoService<EnvironmentSample> {

	@EJB
	private UserService userService;

	public EnvironmentSampleService() {
		super(EnvironmentSample.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, EnvironmentSample> from) {
		return inJurisdictionOrOwned(cb, cq, from);
	}

	@Override
	protected Predicate createRelevantDataFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, EnvironmentSample> from) {
		return CriteriaBuilderHelper.and(cb, super.createRelevantDataFilter(cb, cq, from), createDefaultFilter(cb, from));
	}

	public Predicate createDefaultFilter(CriteriaBuilder cb, From<?, EnvironmentSample> from) {
		return cb.isFalse(from.get(EnvironmentSample.DELETED));
	}

	public boolean isEditAllowed(EnvironmentSample sample) {
		return inJurisdictionOrOwned(sample);
	}

	@Override
	public boolean inJurisdictionOrOwned(EnvironmentSample sample) {
		return fulfillsCondition(sample, this::inJurisdictionOrOwned);
	}

	public Predicate inJurisdictionOrOwned(EnvironmentSampleQueryContext queryContext) {
		return EnvironmentSampleJurisdictionValidator.of(queryContext, getCurrentUser()).inJurisdictionOrOwned();
	}

	@Override
	public List<Long> getInJurisdictionIds(List<EnvironmentSample> samples) {
		return getIdList(samples, this::inJurisdictionOrOwned);
	}

	public Predicate buildCriteriaFilter(EnvironmentSampleCriteria criteria, EnvironmentSampleQueryContext queryContext) {
		From<?, EnvironmentSample> sampleRoot = queryContext.getRoot();
		CriteriaBuilder cb = queryContext.getCriteriaBuilder();
		EnvironmentSampleJoins joins = queryContext.getJoins();

		Predicate filter = null;
		if (criteria.getRelevanceStatus() != null) {
			Path<Boolean> environmentArchived = joins.getEnvironment().get(Environment.ARCHIVED);

			if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.or(cb.isNull(environmentArchived), cb.isFalse(environmentArchived)));
			} else if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.isTrue(environmentArchived));
			} else if (criteria.getRelevanceStatus() == EntityRelevanceStatus.DELETED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(sampleRoot.get(EnvironmentSample.DELETED), true));
			}
		}
		if (criteria.getRelevanceStatus() != EntityRelevanceStatus.DELETED) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isFalse(sampleRoot.get(EnvironmentSample.DELETED)));
		}
		if (criteria.getDispatched() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(sampleRoot.get(EnvironmentSample.DISPATCHED), criteria.getDispatched()));
		}
		if (criteria.getReceived() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(sampleRoot.get(EnvironmentSample.RECEIVED), criteria.getReceived()));
		}
		if (criteria.getFreeText() != null) {
			String[] textFilters = criteria.getFreeText().split("\\s+");
			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.ilike(cb, sampleRoot.get(EnvironmentSample.UUID), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, sampleRoot.get(EnvironmentSample.FIELD_SAMPLE_ID), textFilter),
					CriteriaBuilderHelper.ilike(cb, joins.getEnvironment().get(Environment.UUID), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, joins.getEnvironment().get(Environment.ENVIRONMENT_NAME), textFilter));

				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}
		if (criteria.getRegion() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(joins.getEnvironmentJoins().getLocationJoins().getRegion().get(Region.UUID), criteria.getRegion().getUuid()));
		}
		if (criteria.getDistrict() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(joins.getEnvironmentJoins().getLocationJoins().getDistrict().get(District.UUID), criteria.getDistrict().getUuid()));
		}
		if (criteria.getLaboratory() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getLaboratory().get(Facility.UUID), criteria.getLaboratory().getUuid()));
		}
		if (criteria.getTestedPathogen() != null) {
			throw new UnsupportedOperationException("Tested pathogen is not supported yet");
		}
		if (criteria.getReportDateFrom() != null && criteria.getReportDateTo() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.between(sampleRoot.get(EnvironmentSample.REPORT_DATE), criteria.getReportDateFrom(), criteria.getReportDateTo()));
		} else if (criteria.getReportDateFrom() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.greaterThanOrEqualTo(sampleRoot.get(EnvironmentSample.REPORT_DATE), criteria.getReportDateFrom()));
		} else if (criteria.getReportDateTo() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.lessThanOrEqualTo(sampleRoot.get(EnvironmentSample.REPORT_DATE), criteria.getReportDateTo()));
		}

		filter = CriteriaBuilderHelper.and(
			cb,
			filter,
			EnvironmentService.buildGpsCoordinatesFilter(
				criteria.getGpsLatFrom(),
				criteria.getGpsLatTo(),
				criteria.getGpsLonFrom(),
				criteria.getGpsLonTo(),
				cb,
				joins.getEnvironmentJoins()));

		return filter;
	}

	private Predicate inJurisdictionOrOwned(CriteriaBuilder cb, CriteriaQuery<?> cq, From<?, EnvironmentSample> from) {
		return EnvironmentSampleJurisdictionValidator
			.of(new EnvironmentSampleQueryContext(cb, cq, from, new EnvironmentSampleJoins(from)), userService.getCurrentUser())
			.isRootInJurisdictionOrOwned();
	}
}
