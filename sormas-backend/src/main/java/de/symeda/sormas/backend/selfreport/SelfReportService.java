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

package de.symeda.sormas.backend.selfreport;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import de.symeda.sormas.api.selfreport.SelfReportListEntryDto;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.util.QueryHelper;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.selfreport.SelfReportCriteria;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.location.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Stateless
@LocalBean
public class SelfReportService extends AbstractCoreAdoService<SelfReport, SelfReportJoins> {

	public SelfReportService() {
		super(SelfReport.class, DeletableEntityType.SELF_REPORT);
	}

	@Override
	protected Predicate createUserFilterInternal(CriteriaBuilder cb, CriteriaQuery cq, From<?, SelfReport> from) {
		return null;
	}

	public Predicate createUserFilter(SelfReportQueryContext queryContext) {
		return null;
	}

	@Override
	protected SelfReportJoins toJoins(From<?, SelfReport> adoPath) {
		return new SelfReportJoins(adoPath);
	}

	@Override
	public Predicate inJurisdictionOrOwned(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, SelfReport> from) {
		return cb.conjunction();
	}

	public Predicate buildCriteriaFilter(SelfReportCriteria criteria, SelfReportQueryContext selfReportQueryContext) {
		if (criteria == null) {
			return null;
		}

		CriteriaBuilder cb = selfReportQueryContext.getCriteriaBuilder();
		From<?, SelfReport> from = selfReportQueryContext.getRoot();
		final SelfReportJoins joins = selfReportQueryContext.getJoins();
		Join<SelfReport, Location> location = joins.getAddress();

		Predicate filter = null;

		if (StringUtils.isNotEmpty(criteria.getFreeText())) {
			String[] textFilters = criteria.getFreeText().split("\\s+");
			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.ilike(cb, from.get(SelfReport.CASE_REFERENCE), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, from.get(SelfReport.FIRST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, from.get(SelfReport.LAST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, from.get(SelfReport.EMAIL), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, location.get(Location.STREET), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, location.get(Location.CITY), textFilter),
					CriteriaBuilderHelper.ilike(cb, location.get(Location.POSTAL_CODE), textFilter),
					CriteriaBuilderHelper.ilike(cb, from.get(SelfReport.PHONE_NUMBER), textFilter),
					CriteriaBuilderHelper.ilike(cb, from.get(SelfReport.NATIONAL_HEALTH_ID), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}

		if (criteria.getType() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(SelfReport.TYPE), criteria.getType()));
		}

		if (criteria.getDisease() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(SelfReport.DISEASE), criteria.getDisease()));
		}

		if (criteria.getDiseaseVariant() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(SelfReport.DISEASE_VARIANT), criteria.getDiseaseVariant()));
		}

		if (criteria.getReportDateFrom() != null && criteria.getReportDateTo() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.between(from.get(SelfReport.REPORT_DATE), criteria.getReportDateFrom(), criteria.getReportDateTo()));
		} else if (criteria.getReportDateFrom() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.greaterThanOrEqualTo(from.get(SelfReport.REPORT_DATE), criteria.getReportDateFrom()));
		} else if (criteria.getReportDateTo() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.lessThanOrEqualTo(from.get(SelfReport.REPORT_DATE), criteria.getReportDateTo()));
		}

		if (criteria.getRelevanceStatus() != null) {
			if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = CriteriaBuilderHelper
					.and(cb, filter, cb.or(cb.equal(from.get(SelfReport.ARCHIVED), false), cb.isNull(from.get(SelfReport.ARCHIVED))));
			} else if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(SelfReport.ARCHIVED), true));
			} else if (criteria.getRelevanceStatus() == EntityRelevanceStatus.DELETED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(SelfReport.DELETED), true));
			}
		}
		if (criteria.getRelevanceStatus() != EntityRelevanceStatus.DELETED) {
			filter = CriteriaBuilderHelper.and(cb, filter, createDefaultFilter(cb, from));
		}

		if (criteria.getInvestigationStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(SelfReport.INVESTIGATION_STATUS), criteria.getInvestigationStatus()));
		}

		if (criteria.getCaze() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getCaseJoin().get(Case.UUID), criteria.getCaze().getUuid()));
		}

		if (criteria.getContact() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getContactJoin().get(Contact.UUID), criteria.getContact().getUuid()));
		}

		return filter;
	}

	public Predicate createDefaultFilter(CriteriaBuilder cb, From<?, SelfReport> root) {
		return cb.isFalse(root.get(SelfReport.DELETED));
	}

	public List<SelfReportListEntryDto> getEntriesList(SelfReportCriteria selfReportCriteria, Integer first, Integer max) {
		if (selfReportCriteria == null) {
			return Collections.emptyList();
		}

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		final Root<SelfReport> selfReport = cq.from(SelfReport.class);

		SelfReportQueryContext selfReportQueryContext = new SelfReportQueryContext(cb, cq, selfReport);
		SelfReportJoins joins = selfReportQueryContext.getJoins();

		cq.distinct(true);

		List<Selection<?>> selections = new ArrayList<>(
			Arrays.asList(
				selfReport.get(SelfReport.UUID),
				selfReport.get(SelfReport.REPORT_DATE),
				selfReport.get(SelfReport.CASE_REFERENCE),
				selfReport.get(SelfReport.DISEASE),
				selfReport.get(SelfReport.DATE_OF_TEST)));

		cq.multiselect(selections);

		Predicate filter = CriteriaBuilderHelper.and(cb, createDefaultFilter(cb, selfReport), createUserFilter(selfReportQueryContext));

		Predicate criteriaFilter = buildCriteriaFilter(selfReportCriteria, selfReportQueryContext);

		filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);

		if (filter != null) {
			cq.where(filter);
		}

		List<SelfReportListEntryDto> resultList = QueryHelper.getResultList(em, cq, new SelfReportEntryDtoResultTransformer(), first, max);
		return resultList;
	}
}
