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

import static java.util.Objects.isNull;
import static java.util.function.Predicate.not;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.common.progress.ProcessedEntityStatus;
import de.symeda.sormas.api.deletionconfiguration.DeletionInfoDto;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleCriteria;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleFacade;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleIndexDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleReferenceDto;
import de.symeda.sormas.api.environment.environmentsample.Pathogen;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.common.AbstractBaseEjb;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.environment.Environment;
import de.symeda.sormas.backend.environment.EnvironmentFacadeEjb;
import de.symeda.sormas.backend.environment.EnvironmentService;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.facility.FacilityService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTest;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "EnvironmentSampleFacade")
@RightsAllowed(UserRight._ENVIRONMENT_SAMPLE_VIEW)
public class EnvironmentSampleFacadeEjb
	extends
	AbstractBaseEjb<EnvironmentSample, EnvironmentSampleDto, EnvironmentSampleIndexDto, EnvironmentSampleReferenceDto, EnvironmentSampleService, EnvironmentSampleCriteria>
	implements EnvironmentSampleFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private EnvironmentService environmentService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private LocationFacadeEjbLocal locationFacade;

	public EnvironmentSampleFacadeEjb() {
	}

	@Inject
	public EnvironmentSampleFacadeEjb(EnvironmentSampleService service) {
		super(EnvironmentSample.class, EnvironmentSampleDto.class, service);
	}

	@Override
	@RightsAllowed({
		UserRight._ENVIRONMENT_SAMPLE_CREATE,
		UserRight._ENVIRONMENT_SAMPLE_EDIT })
	public EnvironmentSampleDto save(@Valid @NotNull EnvironmentSampleDto dto) {
		EnvironmentSample existingSample = dto.getUuid() != null ? service.getByUuid(dto.getUuid()) : null;

		validateUserRights(dto, existingSample);

		EnvironmentSampleDto existingDto = toDto(existingSample);
		Pseudonymizer pseudonymizer = createPseudonymizer();
		restorePseudonymizedDto(dto, existingDto, existingSample, pseudonymizer);

		validate(dto);

		EnvironmentSample sample = fillOrBuildEntity(dto, existingSample, true);
		service.ensurePersisted(sample);

		return toPseudonymizedDto(sample, pseudonymizer);
	}

	@Override
	public long count(EnvironmentSampleCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<EnvironmentSample> from = cq.from(EnvironmentSample.class);
		EnvironmentSampleQueryContext queryContext = new EnvironmentSampleQueryContext(cb, cq, from, new EnvironmentSampleJoins(from));

		Predicate filter = service.createUserFilter(cb, cq, from);
		if (criteria != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, service.buildCriteriaFilter(criteria, queryContext));
		} else {
			filter = CriteriaBuilderHelper.and(cb, filter, service.createDefaultFilter(cb, from));
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(from));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public List<EnvironmentSampleIndexDto> getIndexList(
		EnvironmentSampleCriteria criteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {
		List<Long> indexListIds = getIndexListIds(criteria, first, max, sortProperties);

		CriteriaBuilder cb = em.getCriteriaBuilder();

		List<EnvironmentSampleIndexDto> indexList = new ArrayList<>();
		IterableHelper.executeBatched(indexListIds, ModelConstants.PARAMETER_LIMIT, batchedIds -> {
			CriteriaQuery<EnvironmentSampleIndexDto> cq = cb.createQuery(EnvironmentSampleIndexDto.class);
			Root<EnvironmentSample> from = cq.from(EnvironmentSample.class);
			EnvironmentSampleJoins joins = new EnvironmentSampleJoins(from);
			EnvironmentSampleQueryContext queryContext = new EnvironmentSampleQueryContext(cb, cq, from, joins);
			Join<EnvironmentSample, Location> location = joins.getLocation();

			// Tests count subquery
			Subquery<Long> numberOfTests = cq.subquery(Long.class);
			Root<PathogenTest> numberOfTestsRoot = numberOfTests.from(PathogenTest.class);
			numberOfTests.where(
				cb.equal(numberOfTestsRoot.get(PathogenTest.ENVIRONMENT_SAMPLE), from),
				cb.isFalse(numberOfTestsRoot.get(PathogenTest.DELETED)));
			numberOfTests.select(cb.countDistinct(numberOfTestsRoot.get(PathogenTest.ID)));

			cq.multiselect(
				from.get(EnvironmentSample.ID),
				from.get(EnvironmentSample.UUID),
				from.get(EnvironmentSample.FIELD_SAMPLE_ID),
				from.get(EnvironmentSample.SAMPLE_DATE_TIME),
				joins.getEnvironment().get(Environment.ENVIRONMENT_NAME),
				location.get(Location.STREET),
				location.get(Location.HOUSE_NUMBER),
				location.get(Location.POSTAL_CODE),
				location.get(Location.CITY),
				joins.getLocationJoins().getDistrict().get(District.NAME),
				from.get(EnvironmentSample.DISPATCHED),
				from.get(EnvironmentSample.DISPATCH_DATE),
				from.get(EnvironmentSample.RECEIVED),
				from.get(EnvironmentSample.RECEIVAL_DATE),
				joins.getLaboratory().get(Facility.UUID),
				joins.getLaboratory().get(Facility.NAME),
				from.get(EnvironmentSample.LABORATORY_DETAILS),
				from.get(EnvironmentSample.SPECIMEN_CONDITION),
				from.get(EnvironmentSample.SAMPLE_MATERIAL),
				from.get(EnvironmentSample.OTHER_SAMPLE_MATERIAL),
				from.get(EnvironmentSample.DELETION_REASON),
				from.get(EnvironmentSample.OTHER_DELETION_REASON),
				numberOfTests.getSelection(),
				JurisdictionHelper.booleanSelector(cb, service.inJurisdictionOrOwned(queryContext)));

			cq.where(from.get(EnvironmentSample.ID).in(batchedIds));
			sortBy(sortProperties, queryContext);

			List<EnvironmentSampleIndexDto> samples = QueryHelper.getResultList(em, cq, null, null);

			loadAndSetPositivePathogens(cb, samples);
			loadAndSetLatestTest(cb, samples);

			indexList.addAll(samples);
		});

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		pseudonymizer.pseudonymizeDtoCollection(EnvironmentSampleIndexDto.class, indexList, EnvironmentSampleIndexDto::isInJurisdiction, null);

		return indexList;
	}

	private void loadAndSetPositivePathogens(CriteriaBuilder cb, List<EnvironmentSampleIndexDto> samples) {
		List<Long> sampleIds = samples.stream().map(EnvironmentSampleIndexDto::getId).collect(Collectors.toList());

		CriteriaQuery<Tuple> positivePathogensCq = cb.createTupleQuery();
		Root<PathogenTest> positivePathogensRoot = positivePathogensCq.from(PathogenTest.class);
		Path<Object> pathogenTestSampledId = positivePathogensRoot.get(PathogenTest.ENVIRONMENT_SAMPLE).get(EnvironmentSample.ID);
		positivePathogensCq.where(
			pathogenTestSampledId.in(sampleIds),
			cb.isFalse(positivePathogensRoot.get(PathogenTest.DELETED)),
			cb.equal(positivePathogensRoot.get(PathogenTest.TEST_RESULT), PathogenTestResultType.POSITIVE));
		positivePathogensCq.multiselect(pathogenTestSampledId, positivePathogensRoot.get(PathogenTest.TESTED_PATHOGEN));

		List<Tuple> positivePathogens = QueryHelper.getResultList(em, positivePathogensCq, null, null);
		positivePathogens.stream()
			.collect(Collectors.groupingBy(t -> (Long) t.get(0), Collectors.mapping(t -> (Pathogen) t.get(1), Collectors.toList())))
			.forEach(
				(sampleId, pathogens) -> samples.stream()
					.filter(s -> s.getId().equals(sampleId))
					.findFirst()
					.ifPresent(s -> s.setPositivePathogenTests(pathogens)));
	}

	private void loadAndSetLatestTest(CriteriaBuilder cb, List<EnvironmentSampleIndexDto> samples) {
		List<Long> sampleIds = samples.stream().map(EnvironmentSampleIndexDto::getId).collect(Collectors.toList());

		CriteriaQuery<Tuple> testCq = cb.createTupleQuery();
		Root<PathogenTest> testRoot = testCq.from(PathogenTest.class);
		Expression<String> sampleIdExpr = testRoot.get(PathogenTest.ENVIRONMENT_SAMPLE).get(Sample.ID);

		testCq.multiselect(testRoot.get(PathogenTest.TESTED_PATHOGEN), testRoot.get(PathogenTest.TEST_RESULT), sampleIdExpr);

		testCq.where(cb.isFalse(testRoot.get(PathogenTest.DELETED)), sampleIdExpr.in(sampleIds));
		testCq.orderBy(cb.desc(testRoot.get(PathogenTest.CHANGE_DATE)));

		List<Tuple> testList = em.createQuery(testCq).getResultList();

		testList.stream()
			// collecting to map keyed by sample id, keeping the first result that is the latest test for each sample
			.collect(Collectors.toMap(pathogenTest -> (Long) pathogenTest.get(2), Function.identity(), (t1, t2) -> t1))
			.forEach((sampleId, t) -> samples.stream().filter(s -> s.getId().equals(sampleId)).findFirst().ifPresent(s -> {
				s.setLatestTestedPathogen((Pathogen) t.get(0));
				s.setLatestPathogenTestResult((PathogenTestResultType) t.get(1));
			}));
	}

	private List<Long> getIndexListIds(EnvironmentSampleCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<EnvironmentSample> from = cq.from(EnvironmentSample.class);

		EnvironmentSampleQueryContext queryContext = new EnvironmentSampleQueryContext(cb, cq, from, new EnvironmentSampleJoins(from));

		List<Selection<?>> selections = new ArrayList<>();
		selections.add(from.get(EnvironmentSample.ID));
		selections.addAll(sortBy(sortProperties, queryContext));

		cq.multiselect(selections);

		Predicate filter = service.createUserFilter(cb, cq, from);
		if (criteria != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, service.buildCriteriaFilter(criteria, queryContext));
		} else {
			filter = CriteriaBuilderHelper.and(cb, filter, service.createDefaultFilter(cb, from));
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.distinct(true);

		List<Tuple> samples = QueryHelper.getResultList(em, cq, first, max);
		return samples.stream().map(t -> t.get(0, Long.class)).collect(Collectors.toList());
	}

	private List<Selection<?>> sortBy(List<SortProperty> sortProperties, EnvironmentSampleQueryContext queryContext) {

		List<Selection<?>> selections = new ArrayList<>();
		CriteriaBuilder cb = queryContext.getCriteriaBuilder();
		CriteriaQuery<?> cq = queryContext.getQuery();

		if (sortProperties != null && !sortProperties.isEmpty()) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;

				/*
				 * joins.getLocationJoins().getDistrict().get(District.NAME),
				 * joins.getLaboratory().get(Facility.NAME),
				 */

				switch (sortProperty.propertyName) {
				case EnvironmentSampleIndexDto.UUID:
				case EnvironmentSampleIndexDto.FIELD_SAMPLE_ID:
				case EnvironmentSampleIndexDto.SAMPLE_DATE_TIME:
				case EnvironmentSampleIndexDto.DISPATCHED:
				case EnvironmentSampleIndexDto.DISPATCH_DATE:
				case EnvironmentSampleIndexDto.RECEIVED:
				case EnvironmentSampleIndexDto.SAMPLE_MATERIAL:
					expression = queryContext.getRoot().get(sortProperty.propertyName);
					break;
				case EnvironmentSampleIndexDto.ENVIRONMENT:
					expression = queryContext.getJoins().getEnvironment().get(Environment.ENVIRONMENT_NAME);
					break;
				case EnvironmentSampleIndexDto.LOCATION:
					Join<EnvironmentSample, Location> location = queryContext.getJoins().getLocation();
					expression = cb.concat(
						cb.concat(cb.concat(location.get(Location.STREET), location.get(Location.HOUSE_NUMBER)), location.get(Location.POSTAL_CODE)),
						location.get(Location.CITY));
					break;
				case EnvironmentSampleIndexDto.DISTRICT:
					expression = queryContext.getJoins().getLocationJoins().getDistrict().get(District.NAME);
					break;
				case EnvironmentSampleIndexDto.LABORATORY:
					expression = queryContext.getJoins().getLaboratory().get(Facility.NAME);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
				selections.add(expression);
			}
			cq.orderBy(order);
		} else {
			Path<Object> changeDate = queryContext.getRoot().get(EnvironmentSample.CHANGE_DATE);
			cq.orderBy(cb.desc(changeDate));
			selections.add(changeDate);
		}

		return selections;
	}

	@Override
	public void validate(EnvironmentSampleDto dto) throws ValidationRuntimeException {
		Facility laboratory = facilityService.getByReferenceDto(dto.getLaboratory());

		if (dto.getLocation().getRegion() == null) {
			throw new ValidationRuntimeException(
				I18nProperties
					.getValidationError(Validations.required, I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.REGION)));
		}

		if (dto.getLocation().getDistrict() == null) {
			throw new ValidationRuntimeException(
				I18nProperties
					.getValidationError(Validations.required, I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.DISTRICT)));
		}

		if (dto.getLocation().getLatitude() == null) {
			throw new ValidationRuntimeException(
				I18nProperties
					.getValidationError(Validations.required, I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.LATITUDE)));
		}

		if (dto.getLocation().getLongitude() == null) {
			throw new ValidationRuntimeException(
				I18nProperties
					.getValidationError(Validations.required, I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.LONGITUDE)));
		}

		if (laboratory == null
			|| (!FacilityDto.OTHER_FACILITY_UUID.equals(laboratory.getUuid()) && laboratory.getType() != FacilityType.LABORATORY)) {
			throw new ValidationRuntimeException(I18nProperties.getString(Validations.validLaboratory));
		}
	}

	private void validateUserRights(EnvironmentSampleDto sample, EnvironmentSample existingSample) throws ValidationRuntimeException {
		FacadeHelper.checkCreateAndEditRights(existingSample, userService, UserRight.ENVIRONMENT_SAMPLE_CREATE, UserRight.ENVIRONMENT_SAMPLE_EDIT);

		if (existingSample != null) {
			if (!isEditAllowed(existingSample)) {
				throw new AccessDeniedException(I18nProperties.getString(Strings.errorEnvironmentSampleNotEditable));
			}

			if (!userService.hasRight(UserRight.ENVIRONMENT_SAMPLE_EDIT_DISPATCH)
				&& (existingSample.isDispatched() != sample.isDispatched()
					|| !DataHelper.equal(existingSample.getDispatchDate(), sample.getDispatchDate())
					|| !DataHelper.equal(existingSample.getDispatchDetails(), sample.getDispatchDetails()))) {
				throw new AccessDeniedException(I18nProperties.getString(Strings.errorEnvironmentSampleNoDispatchRight));
			}

			if (!userService.hasRight(UserRight.ENVIRONMENT_SAMPLE_EDIT_RECEIVAL)
				&& (existingSample.isReceived() != sample.isReceived()
					|| !DataHelper.equal(existingSample.getReceivalDate(), sample.getReceivalDate()))) {

				throw new AccessDeniedException(I18nProperties.getString(Strings.errorEnvironmentSampleNoReceivalRight));

			}
		}
	}

	@Override
	@RightsAllowed(UserRight._ENVIRONMENT_SAMPLE_DELETE)
	public void delete(String uuid, DeletionDetails deletionDetails) {
		EnvironmentSample sample = service.getByUuid(uuid);
		delete(sample, deletionDetails);
	}

	private void delete(EnvironmentSample sample, DeletionDetails deletionDetails) {
		if (!service.inJurisdictionOrOwned(sample)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.messageEnvironmentSampleOutsideJurisdictionDeletionDenied));
		}

		service.delete(sample, deletionDetails);
	}

	@Override
	@RightsAllowed(UserRight._ENVIRONMENT_SAMPLE_DELETE)
	public List<ProcessedEntity> delete(List<String> uuids, DeletionDetails deletionDetails) {
		List<ProcessedEntity> processedSamples = new ArrayList<>();
		List<EnvironmentSample> samplesToDelete = service.getByUuids(uuids);

		if (samplesToDelete != null) {
			samplesToDelete.stream().filter(not(EnvironmentSample::isDeleted)).forEach(sample -> {
				try {
					delete(sample.getUuid(), deletionDetails);
					processedSamples.add(new ProcessedEntity(sample.getUuid(), ProcessedEntityStatus.SUCCESS));
				} catch (Exception e) {
					processedSamples.add(new ProcessedEntity(sample.getUuid(), ProcessedEntityStatus.INTERNAL_FAILURE));
					logger.error("The environment sample with uuid: {} could not be deleted", sample.getUuid(), e);
				}
			});
		}
		return processedSamples;
	}

	@Override
	@RightsAllowed(UserRight._ENVIRONMENT_SAMPLE_DELETE)
	public void restore(String uuid) {
		EnvironmentSample sample = service.getByUuid(uuid);
		restore(sample);
	}

	private void restore(EnvironmentSample sample) {
		service.restore(sample);
	}

	@Override
	@RightsAllowed(UserRight._ENVIRONMENT_SAMPLE_DELETE)
	public List<ProcessedEntity> restore(List<String> uuids) {
		List<ProcessedEntity> processedSamples = new ArrayList<>();
		List<EnvironmentSample> samplesToRestore = service.getByUuids(uuids);

		if (samplesToRestore != null) {
			samplesToRestore.stream().filter(EnvironmentSample::isDeleted).forEach(sample -> {
				try {
					restore(sample);
					processedSamples.add(new ProcessedEntity(sample.getUuid(), ProcessedEntityStatus.SUCCESS));
				} catch (Exception e) {
					processedSamples.add(new ProcessedEntity(sample.getUuid(), ProcessedEntityStatus.INTERNAL_FAILURE));
					logger.error("The sample with uuid: {} could not be restored", sample.getUuid(), e);
				}
			});
		}
		return processedSamples;
	}

	@Override
	public boolean isDeleted(String uuid) {
		return service.isDeleted(uuid);
	}

	@Override
	public boolean exists(String uuid) {
		return service.exists(uuid);
	}

	@Override
	public boolean isEditAllowed(String uuid) {
		return service.isEditAllowed(service.getByUuid(uuid));
	}

	@Override
	public EditPermissionType getEditPermissionType(String sampleUuid) {
		return service.getEditPermissionType(service.getByUuid(sampleUuid));
	}

	@Override
	public DeletionInfoDto getAutomaticDeletionInfo(String sampleUuid) {
		return service.getAutomaticDeletionInfo(sampleUuid);
	}

	@Override
	public DeletionInfoDto getManuallyDeletionInfo(String sampleUuid) {
		return service.getManuallyDeletionInfo(sampleUuid);
	}

	@Override
	protected EnvironmentSample fillOrBuildEntity(EnvironmentSampleDto source, EnvironmentSample target, boolean checkChangeDate) {
		boolean targetWasNull = isNull(target);
		target = DtoHelper.fillOrBuildEntity(source, target, EnvironmentSample::new, checkChangeDate);

		if (targetWasNull) {
			FacadeHelper.setUuidIfDtoExists(target.getLocation(), source.getLocation());
		}

		target.setEnvironment(environmentService.getByReferenceDto(source.getEnvironment()));
		target.setReportDate(source.getReportDate());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setSampleDateTime(source.getSampleDateTime());
		target.setSampleMaterial(source.getSampleMaterial());
		target.setOtherSampleMaterial(source.getOtherSampleMaterial());
		target.setSampleVolume(source.getSampleVolume());
		target.setFieldSampleId(source.getFieldSampleId());
		target.setTurbidity(source.getTurbidity());
		target.setPhValue(source.getPhValue());
		target.setSampleTemperature(source.getSampleTemperature());
		target.setChlorineResiduals(source.getChlorineResiduals());
		target.setLaboratory(facilityService.getByReferenceDto(source.getLaboratory()));
		target.setLaboratoryDetails(source.getLaboratoryDetails());
		target.setRequestedPathogenTests(source.getRequestedPathogenTests());
		target.setOtherRequestedPathogenTests(source.getOtherRequestedPathogenTests());
		target.setWeatherConditions(source.getWeatherConditions());
		target.setHeavyRain(source.getHeavyRain());
		target.setDispatched(source.isDispatched());
		target.setDispatchDate(source.getDispatchDate());
		target.setDispatchDetails(source.getDispatchDetails());
		target.setReceived(source.isReceived());
		target.setReceivalDate(source.getReceivalDate());
		target.setLabSampleId(source.getLabSampleId());
		target.setSpecimenCondition(source.getSpecimenCondition());
		target.setLocation(locationFacade.fillOrBuildEntity(source.getLocation(), target.getLocation(), checkChangeDate));
		target.setGeneralComment(source.getGeneralComment());
		target.setDeleted(source.isDeleted());
		target.setDeletionReason(source.getDeletionReason());
		target.setOtherDeletionReason(source.getOtherDeletionReason());

		return target;
	}

	@Override
	protected EnvironmentSampleDto toDto(EnvironmentSample source) {
		if (source == null) {
			return null;
		}
		EnvironmentSampleDto target = new EnvironmentSampleDto();
		DtoHelper.fillDto(target, source);

		target.setEnvironment(EnvironmentFacadeEjb.toReferenceDto(source.getEnvironment()));
		target.setReportDate(source.getReportDate());
		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setSampleDateTime(source.getSampleDateTime());
		target.setSampleMaterial(source.getSampleMaterial());
		target.setOtherSampleMaterial(source.getOtherSampleMaterial());
		target.setSampleVolume(source.getSampleVolume());
		target.setFieldSampleId(source.getFieldSampleId());
		target.setTurbidity(source.getTurbidity());
		target.setPhValue(source.getPhValue());
		target.setSampleTemperature(source.getSampleTemperature());
		target.setChlorineResiduals(source.getChlorineResiduals());
		target.setLaboratory(FacilityFacadeEjb.toReferenceDto(source.getLaboratory()));
		target.setLaboratoryDetails(source.getLaboratoryDetails());
		target.setRequestedPathogenTests(source.getRequestedPathogenTests());
		target.setOtherRequestedPathogenTests(source.getOtherRequestedPathogenTests());
		target.setWeatherConditions(source.getWeatherConditions());
		target.setHeavyRain(source.getHeavyRain());
		target.setDispatched(source.isDispatched());
		target.setDispatchDate(source.getDispatchDate());
		target.setDispatchDetails(source.getDispatchDetails());
		target.setReceived(source.isReceived());
		target.setReceivalDate(source.getReceivalDate());
		target.setLabSampleId(source.getLabSampleId());
		target.setSpecimenCondition(source.getSpecimenCondition());
		target.setLocation(LocationFacadeEjb.toDto(source.getLocation()));
		target.setGeneralComment(source.getGeneralComment());
		target.setDeleted(source.isDeleted());
		target.setDeletionReason(source.getDeletionReason());
		target.setOtherDeletionReason(source.getOtherDeletionReason());

		return target;
	}

	public static EnvironmentSampleReferenceDto toReferenceDto(EnvironmentSample environmentSample) {

		if (environmentSample == null) {
			return null;
		}

		return new EnvironmentSampleReferenceDto(
			environmentSample.getUuid(),
			environmentSample.getSampleMaterial(),
			environmentSample.getEnvironment().getUuid());
	}

	@Override
	protected EnvironmentSampleReferenceDto toRefDto(EnvironmentSample environmentSample) {
		return new EnvironmentSampleReferenceDto(
			environmentSample.getUuid(),
			environmentSample.getSampleMaterial(),
			environmentSample.getEnvironment().getUuid());
	}

	@Override
	protected void pseudonymizeDto(EnvironmentSample source, EnvironmentSampleDto dto, Pseudonymizer pseudonymizer, boolean inJurisdiction) {
		if (dto != null) {
			pseudonymizer.pseudonymizeDto(EnvironmentSampleDto.class, dto, inJurisdiction, e -> {
				pseudonymizer.pseudonymizeDto(EnvironmentReferenceDto.class, e.getEnvironment(), inJurisdiction, null);
				pseudonymizer.pseudonymizeUser(source.getReportingUser(), userService.getCurrentUser(), dto::setReportingUser);
			});
		}
	}

	@Override
	protected void restorePseudonymizedDto(
		EnvironmentSampleDto dto,
		EnvironmentSampleDto existingDto,
		EnvironmentSample entity,
		Pseudonymizer pseudonymizer) {
		if (existingDto != null) {
			boolean inJurisdiction = service.inJurisdictionOrOwned(entity);
			pseudonymizer.restorePseudonymizedValues(EnvironmentSampleDto.class, dto, existingDto, inJurisdiction);
			pseudonymizer.restoreUser(entity.getReportingUser(), userService.getCurrentUser(), dto, dto::setReportingUser);
		}

	}

	private boolean isEditAllowed(EnvironmentSample sample) {
		return service.isEditAllowed(sample);
	}

	@LocalBean
	@Stateless
	public static class EnvironmentSampleFacadeEjbLocal extends EnvironmentSampleFacadeEjb {

		public EnvironmentSampleFacadeEjbLocal() {
			super();
		}

		@Inject
		public EnvironmentSampleFacadeEjbLocal(EnvironmentSampleService service) {
			super(service);
		}
	}
}
