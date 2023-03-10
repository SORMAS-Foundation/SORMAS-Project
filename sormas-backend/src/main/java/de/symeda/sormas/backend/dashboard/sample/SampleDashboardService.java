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

package de.symeda.sormas.backend.dashboard.sample;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.tuple.Pair;

import de.symeda.sormas.api.dashboard.SampleDashboardCriteria;
import de.symeda.sormas.api.dashboard.sample.SampleShipmentStatus;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDashboardFilterDateType;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.sample.PathogenTest;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleJoins;
import de.symeda.sormas.backend.sample.SampleQueryContext;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless
@LocalBean
public class SampleDashboardService {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private SampleService sampleService;

	public Map<PathogenTestResultType, Long> getSampleCountsByResultType(SampleDashboardCriteria dashboardCriteria) {
		return getSampleCountsByEnumProperty(Sample.PATHOGEN_TEST_RESULT, PathogenTestResultType.class, dashboardCriteria, null);
	}

	public Map<SamplePurpose, Long> getSampleCountsByPurpose(SampleDashboardCriteria dashboardCriteria) {
		return getSampleCountsByEnumProperty(Sample.SAMPLE_PURPOSE, SamplePurpose.class, dashboardCriteria, null);
	}

	private static Map<Pair<Boolean, Boolean>, SampleShipmentStatus> shipmentStatusMapping = new HashMap<>();

	static {
		shipmentStatusMapping.put(Pair.of(true, true), SampleShipmentStatus.RECEIVED);
		shipmentStatusMapping.put(Pair.of(true, false), SampleShipmentStatus.SHIPPED);
		shipmentStatusMapping.put(Pair.of(false, true), SampleShipmentStatus.RECEIVED);
		shipmentStatusMapping.put(Pair.of(false, false), SampleShipmentStatus.NOT_SHIPPED);
	}

	public Map<SpecimenCondition, Long> getSampleCountsBySpecimenCondition(SampleDashboardCriteria dashboardCriteria) {
		return getSampleCountsByEnumProperty(
			Sample.SPECIMEN_CONDITION,
			SpecimenCondition.class,
			dashboardCriteria,
			(cb, root) -> cb.and(buildExternalSamplePredicate(cb, root), cb.equal(root.get(Sample.RECEIVED), true)));
	}

	private <T extends Enum<?>> Map<T, Long> getSampleCountsByEnumProperty(
		String property,
		Class<T> propertyType,
		SampleDashboardCriteria dashboardCriteria,
		BiFunction<CriteriaBuilder, Root<Sample>, Predicate> additionalFilters) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		final Root<Sample> sample = cq.from(Sample.class);
		final Path<Object> groupingProperty = sample.get(property);

		cq.multiselect(groupingProperty, cb.count(sample));

		final Predicate criteriaFilter = createSampleFilter(new SampleQueryContext(cb, cq, sample), dashboardCriteria);
		cq.where(CriteriaBuilderHelper.and(cb, criteriaFilter, additionalFilters != null ? additionalFilters.apply(cb, sample) : null));

		cq.groupBy(groupingProperty);

		return QueryHelper.getResultList(em, cq, null, null, Function.identity())
			.stream()
			.collect(Collectors.toMap(t -> propertyType.cast(t.get(0)), t -> (Long) t.get(1)));
	}

	private Predicate buildExternalSamplePredicate(CriteriaBuilder cb, Root<Sample> root) {
		return cb.equal(root.get(Sample.SAMPLE_PURPOSE), SamplePurpose.EXTERNAL);
	}

	public Map<SampleShipmentStatus, Long> getSampleCountsByShipmentStatus(SampleDashboardCriteria dashboardCriteria) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		final Root<Sample> sample = cq.from(Sample.class);

		Path<Boolean> shipped = sample.get(Sample.SHIPPED);
		Path<Boolean> received = sample.get(Sample.RECEIVED);
		cq.multiselect(shipped, received, cb.count(sample));

		final Predicate criteriaFilter = createSampleFilter(new SampleQueryContext(cb, cq, sample), dashboardCriteria);
		cq.where(CriteriaBuilderHelper.and(cb, criteriaFilter, buildExternalSamplePredicate(cb, sample)));

		cq.groupBy(shipped, received);

		return QueryHelper.getResultList(em, cq, null, null, Function.identity())
			.stream()
			.collect(Collectors.toMap(t -> getSampleShipmentStatusByFlags((Boolean) t.get(0), (Boolean) t.get(1)), t -> (Long) t.get(2), Long::sum));
	}

	private SampleShipmentStatus getSampleShipmentStatusByFlags(Boolean shipped, Boolean received) {
		return shipmentStatusMapping.get(Pair.of(Boolean.TRUE.equals(shipped), Boolean.TRUE.equals(received)));
	}

	private <T extends AbstractDomainObject> Predicate createSampleFilter(SampleQueryContext queryContext, SampleDashboardCriteria criteria) {
		CriteriaBuilder cb = queryContext.getCriteriaBuilder();
		CriteriaQuery<?> cq = queryContext.getQuery();
		From<?, Sample> sampleRoot = queryContext.getRoot();
		SampleJoins joins = queryContext.getJoins();

		Predicate filter = sampleService.buildCriteriaFilter(
			new SampleCriteria().disease(criteria.getDisease()).region(criteria.getRegion()).district(criteria.getDistrict()),
			queryContext);

		if (criteria.getDateFrom() != null && criteria.getDateTo() != null) {
			final Predicate dateFilter;
			Date dateFrom = DateHelper.getStartOfDay(criteria.getDateFrom());
			Date dateTo = DateHelper.getEndOfDay(criteria.getDateTo());

			SampleDashboardFilterDateType sampleDateType =
				criteria.getSampleDateType() != null ? criteria.getSampleDateType() : SampleDashboardFilterDateType.MOST_RELEVANT;

			switch (sampleDateType) {
			case SAMPLE_DATE_TIME:
				dateFilter = cb.between(sampleRoot.get(Sample.SAMPLE_DATE_TIME), dateFrom, dateTo);
				break;
			case ASSOCIATED_ENTITY_REPORT_DATE:
				dateFilter = cb.or(
					cb.between(joins.getCaze().get(Case.REPORT_DATE), dateFrom, dateTo),
					cb.between(joins.getContact().get(Contact.REPORT_DATE_TIME), dateFrom, dateTo),
					cb.between(joins.getEvent().get(Event.REPORT_DATE_TIME), dateFrom, dateTo));
				break;
			case MOST_RELEVANT:
				Subquery<Date> pathogenTestSq = cq.subquery(Date.class);
				Root<PathogenTest> pathogenTestRoot = pathogenTestSq.from(PathogenTest.class);
				Path<Number> pathogenTestDate = pathogenTestRoot.get(PathogenTest.TEST_DATE_TIME);
				pathogenTestSq.select((Expression<Date>) (Expression<?>) cb.max(pathogenTestDate));
				pathogenTestSq.where(cb.equal(pathogenTestRoot.get(PathogenTest.SAMPLE), sampleRoot));

				dateFilter = cb.between(
					CriteriaBuilderHelper.coalesce(cb, Date.class, pathogenTestSq, sampleRoot.get(Sample.SAMPLE_DATE_TIME)),
					dateFrom,
					dateTo);
				break;
			default:
				throw new RuntimeException("Unhandled date type [" + sampleDateType + "]");
			}

			filter = CriteriaBuilderHelper.and(cb, filter, dateFilter);
		}

		if (criteria.getSampleMaterial() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(sampleRoot.get(Sample.SAMPLE_MATERIAL), criteria.getSampleMaterial()));
		}

		if (Boolean.TRUE.equals(criteria.getWithNoDisease())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isNotNull(joins.getEventParticipant()), cb.isNull(joins.getEvent().get(Event.DISEASE)));
		} else if (Boolean.FALSE.equals(criteria.getWithNoDisease())) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.or(cb.isNull(joins.getEventParticipant()), cb.isNotNull(joins.getEvent().get(Event.DISEASE))));
		}

		return CriteriaBuilderHelper.and(
			cb,
			filter,
			// Exclude deleted cases. Archived cases should stay included
			cb.isFalse(sampleRoot.get(Case.DELETED)));
	}
}
