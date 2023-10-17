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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import de.symeda.sormas.api.dashboard.SampleDashboardCriteria;
import de.symeda.sormas.api.dashboard.sample.MapSampleDto;
import de.symeda.sormas.api.dashboard.sample.SampleShipmentStatus;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleCriteria;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleAssociationType;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDashboardFilterDateType;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.environment.Environment;
import de.symeda.sormas.backend.environment.environmentsample.EnvironmentSample;
import de.symeda.sormas.backend.environment.environmentsample.EnvironmentSampleJoins;
import de.symeda.sormas.backend.environment.environmentsample.EnvironmentSampleQueryContext;
import de.symeda.sormas.backend.environment.environmentsample.EnvironmentSampleService;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.sample.ISampleJoins;
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

	private static final String ENVIRONMENT_ASSOCIATION_TYPE = "environment";

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private SampleService sampleService;
	private static final Map<String, CoordinatesExtractor<? extends ISampleJoins>> coordinatesExtractors = new HashMap<>();

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

	static {
		coordinatesExtractors.put(
			SampleAssociationType.CASE.name(),
			new CoordinatesExtractor<SampleJoins>(SampleJoins::getCasePersonAddress, SampleJoins::getCaze, Case.REPORT_LON, Case.REPORT_LAT));
		coordinatesExtractors.put(
			SampleAssociationType.CONTACT.name(),
			new CoordinatesExtractor<SampleJoins>(
				SampleJoins::getContactPersonAddress,
				SampleJoins::getContact,
				Contact.REPORT_LON,
				Contact.REPORT_LAT));
		coordinatesExtractors.put(
			SampleAssociationType.EVENT_PARTICIPANT.name(),
			new CoordinatesExtractor<SampleJoins>(
				SampleJoins::getEventParticipantAddress,
				SampleJoins::getEvent,
				Event.REPORT_LON,
				Event.REPORT_LAT));
		coordinatesExtractors.put(
			ENVIRONMENT_ASSOCIATION_TYPE,
			new CoordinatesExtractor<EnvironmentSampleJoins>(
				EnvironmentSampleJoins::getLocation,
				EnvironmentSampleJoins::getEnvironmentLocation,
				Location.LONGITUDE,
				Location.LATITUDE));
	}

	@EJB
	private EnvironmentSampleService environmentSampleService;

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

	public Map<PathogenTestResultType, Long> getTestResultCountsByResultType(SampleDashboardCriteria dashboardCriteria) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		final Root<Sample> sample = cq.from(Sample.class);
		Join<Sample, PathogenTest> pathogenTestJoin = sample.join(Sample.PATHOGENTESTS, JoinType.LEFT);
		final Path<Object> pathogenTestResult = pathogenTestJoin.get(PathogenTest.TEST_RESULT);

		cq.multiselect(pathogenTestResult, cb.count(pathogenTestJoin));

		final Predicate criteriaFilter = createSampleFilter(new SampleQueryContext(cb, cq, sample), dashboardCriteria);
		cq.where(criteriaFilter);

		cq.groupBy(pathogenTestResult);

		return QueryHelper.getResultList(em, cq, null, null, Function.identity())
			.stream()
			.collect(Collectors.toMap(t -> (PathogenTestResultType) t.get(0), t -> (Long) t.get(1)));
	}

	private static <J extends ISampleJoins> List<Selection<?>> getCoordinatesSelection(
		SampleAssociationType associationType,
		J joins,
		CriteriaBuilder cb,
		Set<SampleAssociationType> allowedAssociationTypes) {
		if (allowedAssociationTypes.contains(associationType)) {
			return getCoordinatesSelection(associationType.name(), joins);
		} else {
			return Arrays
				.asList(cb.nullLiteral(Double.class), cb.nullLiteral(Double.class), cb.nullLiteral(Double.class), cb.nullLiteral(Double.class));
		}
	}

	@NotNull
	private static <J extends ISampleJoins> List<Selection<?>> getCoordinatesSelection(String associationType, J joins) {
		@SuppressWarnings("unchecked")
		CoordinatesExtractor<J> coordinatesExtractor = (CoordinatesExtractor<J>) coordinatesExtractors.get(associationType);
		Path<Location> addressPath = coordinatesExtractor.addressPathProvider.apply(joins);
		Path<?> fallbackPath = coordinatesExtractor.fallbackLocationHolderProvider.apply(joins);

		return Arrays.asList(
			addressPath.get(Location.LONGITUDE),
			addressPath.get(Location.LATITUDE),
			fallbackPath.get(coordinatesExtractor.fallbackLonField),
			fallbackPath.get(coordinatesExtractor.fallbackLatField));
	}

	public List<MapSampleDto> getSamplesForMap(SampleDashboardCriteria criteria, Set<SampleAssociationType> associationTypes) {
		if (associationTypes.isEmpty()) {
			return Collections.emptyList();
		}

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<MapSampleDto> cq = cb.createQuery(MapSampleDto.class);
		final Root<Sample> sample = cq.from(Sample.class);
		SampleJoins joins = new SampleJoins(sample);

		List<Selection<?>> selections = new ArrayList<>();
		selections.addAll(getCoordinatesSelection(SampleAssociationType.CASE, joins, cb, associationTypes));
		selections.addAll(getCoordinatesSelection(SampleAssociationType.CONTACT, joins, cb, associationTypes));
		selections.addAll(getCoordinatesSelection(SampleAssociationType.EVENT_PARTICIPANT, joins, cb, associationTypes));

		cq.multiselect(selections);

		final Predicate criteriaFilter = createSampleFilter(new SampleQueryContext(cb, cq, sample), criteria);
		final Predicate latLonProvided = getLatLonProvidedPredicate(cb, joins, associationTypes);

		cq.where(CriteriaBuilderHelper.and(cb, criteriaFilter, latLonProvided));

		return QueryHelper.getResultList(em, cq, null, null);
	}

	public Long countSamplesForMap(SampleDashboardCriteria criteria, Set<SampleAssociationType> associationTypes) {
		if (associationTypes.isEmpty()) {
			return 0L;
		}

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		final Root<Sample> sample = cq.from(Sample.class);
		SampleJoins joins = new SampleJoins(sample);

		cq.select(cb.count(sample));

		final Predicate criteriaFilter = createSampleFilter(new SampleQueryContext(cb, cq, sample), criteria);
		final Predicate latLonProvided = getLatLonProvidedPredicate(cb, joins, associationTypes);

		cq.where(CriteriaBuilderHelper.and(cb, criteriaFilter, latLonProvided));

		return QueryHelper.getSingleResult(em, cq);
	}

	public Long countEnvironmentSamplesForMap(SampleDashboardCriteria criteria) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		final Root<EnvironmentSample> sample = cq.from(EnvironmentSample.class);
		EnvironmentSampleJoins joins = new EnvironmentSampleJoins(sample);

		cq.select(cb.count(sample));

		final Predicate criteriaFilter = createEnvironmentSampleFilter(new EnvironmentSampleQueryContext(cb, cq, sample, joins), criteria);
		final Predicate latLonProvided = getLatLonProvidedPredicate(cb, joins, ENVIRONMENT_ASSOCIATION_TYPE);

		cq.where(CriteriaBuilderHelper.and(cb, criteriaFilter, latLonProvided));

		return QueryHelper.getSingleResult(em, cq);
	}

	public List<MapSampleDto> getEnvironmentalSamplesForMap(SampleDashboardCriteria criteria) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<MapSampleDto> cq = cb.createQuery(MapSampleDto.class);
		final Root<EnvironmentSample> sample = cq.from(EnvironmentSample.class);
		EnvironmentSampleJoins joins = new EnvironmentSampleJoins(sample);

		List<Selection<?>> selections = new ArrayList<>(getCoordinatesSelection(ENVIRONMENT_ASSOCIATION_TYPE, joins));

		cq.multiselect(selections);

		final Predicate criteriaFilter = createEnvironmentSampleFilter(new EnvironmentSampleQueryContext(cb, cq, sample, joins), criteria);
		final Predicate latLonProvided = getLatLonProvidedPredicate(cb, joins, ENVIRONMENT_ASSOCIATION_TYPE);

		cq.where(CriteriaBuilderHelper.and(cb, criteriaFilter, latLonProvided));

		return QueryHelper.getResultList(em, cq, null, null);
	}

	private Predicate getLatLonProvidedPredicate(CriteriaBuilder cb, SampleJoins joins, Set<SampleAssociationType> associationTypes) {
		List<Predicate> predicates = new ArrayList<>();

		for (SampleAssociationType associationType : associationTypes) {
			predicates.add(getLatLonProvidedPredicate(cb, joins, associationType.name()));
		}

		if (predicates.isEmpty()) {
			predicates.add(cb.disjunction());
		}

		return CriteriaBuilderHelper.or(cb, predicates.toArray(new Predicate[] {}));
	}

	private <J extends ISampleJoins> Predicate getLatLonProvidedPredicate(CriteriaBuilder cb, J joins, String associationType) {
		@SuppressWarnings("unchecked")
		CoordinatesExtractor<J> coordinatesExtractor = (CoordinatesExtractor<J>) coordinatesExtractors.get(associationType);

		Path<Location> addressPath = coordinatesExtractor.addressPathProvider.apply(joins);
		Path<?> fallbackPath = coordinatesExtractor.fallbackLocationHolderProvider.apply(joins);

		return CriteriaBuilderHelper.or(
			cb,
			addressCoordinatesNotNull(cb, addressPath),
			gpsCoordinatesNotNull(cb, fallbackPath, coordinatesExtractor.fallbackLonField, coordinatesExtractor.fallbackLatField));
	}

	private Predicate addressCoordinatesNotNull(CriteriaBuilder cb, Path<Location> addressPath) {
		return gpsCoordinatesNotNull(cb, addressPath, Location.LONGITUDE, Location.LATITUDE);
	}

	private Predicate gpsCoordinatesNotNull(CriteriaBuilder cb, Path<?> path, String longitudeProperty, String latitudeProperty) {
		return CriteriaBuilderHelper.and(cb, cb.isNotNull(path.get(longitudeProperty)), cb.isNotNull(path.get(latitudeProperty)));
	}

	private <T extends AbstractDomainObject> Predicate createSampleFilter(SampleQueryContext queryContext, SampleDashboardCriteria criteria) {
		CriteriaBuilder cb = queryContext.getCriteriaBuilder();
		CriteriaQuery<?> cq = queryContext.getQuery();
		From<?, Sample> sampleRoot = queryContext.getRoot();
		SampleJoins joins = queryContext.getJoins();

		Predicate filter = sampleService.createUserFilter(queryContext, new SampleCriteria().sampleAssociationType(SampleAssociationType.ALL));

		filter = CriteriaBuilderHelper.and(
			cb,
			filter,
			sampleService.buildCriteriaFilter(
				new SampleCriteria().disease(criteria.getDisease()).region(criteria.getRegion()).district(criteria.getDistrict()),
				queryContext));

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
			cb.isFalse(sampleRoot.get(Sample.DELETED)));
	}

	private Predicate createEnvironmentSampleFilter(EnvironmentSampleQueryContext queryContext, SampleDashboardCriteria criteria) {
		CriteriaBuilder cb = queryContext.getCriteriaBuilder();
		CriteriaQuery<?> cq = queryContext.getQuery();
		From<?, EnvironmentSample> sampleRoot = queryContext.getRoot();
		EnvironmentSampleJoins joins = queryContext.getJoins();

		Predicate filter = environmentSampleService.createUserFilter(cb, cq, sampleRoot);

		filter = CriteriaBuilderHelper.and(
			cb,
			filter,
			environmentSampleService
				.buildCriteriaFilter(new EnvironmentSampleCriteria().region(criteria.getRegion()).district(criteria.getDistrict()), queryContext));

		if (criteria.getDateFrom() != null && criteria.getDateTo() != null) {
			final Predicate dateFilter;
			Date dateFrom = DateHelper.getStartOfDay(criteria.getDateFrom());
			Date dateTo = DateHelper.getEndOfDay(criteria.getDateTo());

			SampleDashboardFilterDateType sampleDateType =
				criteria.getSampleDateType() != null ? criteria.getSampleDateType() : SampleDashboardFilterDateType.MOST_RELEVANT;

			switch (sampleDateType) {
			case SAMPLE_DATE_TIME:
				dateFilter = cb.between(sampleRoot.get(EnvironmentSample.SAMPLE_DATE_TIME), dateFrom, dateTo);
				break;
			case ASSOCIATED_ENTITY_REPORT_DATE:
				dateFilter = cb.or(cb.between(joins.getEnvironment().get(Environment.REPORT_DATE), dateFrom, dateTo));
				break;
			case MOST_RELEVANT:
				Subquery<Date> pathogenTestSq = cq.subquery(Date.class);
				Root<PathogenTest> pathogenTestRoot = pathogenTestSq.from(PathogenTest.class);
				Path<Number> pathogenTestDate = pathogenTestRoot.get(PathogenTest.TEST_DATE_TIME);
				pathogenTestSq.select((Expression<Date>) (Expression<?>) cb.max(pathogenTestDate));
				pathogenTestSq.where(cb.equal(pathogenTestRoot.get(PathogenTest.ENVIRONMENT_SAMPLE), sampleRoot));

				dateFilter = cb.between(
					CriteriaBuilderHelper.coalesce(cb, Date.class, pathogenTestSq, sampleRoot.get(EnvironmentSample.SAMPLE_DATE_TIME)),
					dateFrom,
					dateTo);
				break;
			default:
				throw new RuntimeException("Unhandled date type [" + sampleDateType + "]");
			}

			filter = CriteriaBuilderHelper.and(cb, filter, dateFilter);
		}

		if (criteria.getSampleMaterial() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(sampleRoot.get(EnvironmentSample.SAMPLE_MATERIAL), criteria.getSampleMaterial()));
		}

		return CriteriaBuilderHelper.and(
			cb,
			filter,
			// Exclude deleted cases. Archived cases should stay included
			cb.isFalse(sampleRoot.get(EnvironmentSample.DELETED)));
	}

	private final static class CoordinatesExtractor<J extends ISampleJoins> {

		private final Function<J, Path<Location>> addressPathProvider;

		private final Function<J, Path<?>> fallbackLocationHolderProvider;
		private final String fallbackLonField;
		private final String fallbackLatField;

		private CoordinatesExtractor(
			Function<J, Path<Location>> addressPathProvider,
			Function<J, Path<?>> fallbackLocationHolderProvider,
			String fallbackLonField,
			String fallbackLatField) {
			this.addressPathProvider = addressPathProvider;
			this.fallbackLocationHolderProvider = fallbackLocationHolderProvider;
			this.fallbackLonField = fallbackLonField;
			this.fallbackLatField = fallbackLatField;
		}
	}
}
