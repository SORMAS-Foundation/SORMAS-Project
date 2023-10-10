
package de.symeda.sormas.backend.environment;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.NotImplementedException;

import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.environment.EnvironmentCriteria;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentFacade;
import de.symeda.sormas.api.environment.EnvironmentIndexDto;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolRuntimeException;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.common.AbstractCoreFacadeEjb;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "EnvironmentFacade")
@RightsAllowed(UserRight._ENVIRONMENT_VIEW)
public class EnvironmentFacadeEjb
	extends AbstractCoreFacadeEjb<Environment, EnvironmentDto, EnvironmentIndexDto, EnvironmentReferenceDto, EnvironmentService, EnvironmentCriteria>
	implements EnvironmentFacade {

	public EnvironmentFacadeEjb() {
	}

	@Inject
	public EnvironmentFacadeEjb(EnvironmentService service) {
		super(Environment.class, EnvironmentDto.class, service);
	}

	@EJB
	private LocationFacadeEjb.LocationFacadeEjbLocal locationFacade;

	@Override
	@RightsAllowed({
		UserRight._ENVIRONMENT_EDIT,
		UserRight._ENVIRONMENT_CREATE })
	public EnvironmentDto save(EnvironmentDto dto) {
		return save(dto, true);
	}

	@RightsAllowed({
		UserRight._ENVIRONMENT_EDIT,
		UserRight._ENVIRONMENT_CREATE })
	public EnvironmentDto save(EnvironmentDto dto, boolean checkChangeDate) {

		Environment existingEnvironment = dto.getUuid() != null ? service.getByUuid(dto.getUuid()) : null;

		FacadeHelper.checkCreateAndEditRights(existingEnvironment, userService, UserRight.ENVIRONMENT_CREATE, UserRight.ENVIRONMENT_EDIT);

		validate(dto);
		Environment environment = fillOrBuildEntity(dto, existingEnvironment, checkChangeDate);
		service.ensurePersisted(environment);

		return toDto(environment);
	}

	@Override
	public long count(EnvironmentCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Environment> environment = cq.from(Environment.class);

		final EnvironmentQueryContext environmentQueryContext = new EnvironmentQueryContext(cb, cq, environment);

		Predicate filter = CriteriaBuilderHelper
			.and(cb, service.createUserFilter(environmentQueryContext), service.buildCriteriaFilter(criteria, environmentQueryContext));
		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(environment));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public List<EnvironmentIndexDto> getIndexList(EnvironmentCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		List<Long> indexListIds = getIndexListIds(criteria, first, max, sortProperties);

		List<EnvironmentIndexDto> environments = new ArrayList<>();

		IterableHelper.executeBatched(indexListIds, ModelConstants.PARAMETER_LIMIT, batchedIds -> {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EnvironmentIndexDto> cq = cb.createQuery(EnvironmentIndexDto.class);
			Root<Environment> environment = cq.from(Environment.class);

			final EnvironmentQueryContext environmentQueryContext = new EnvironmentQueryContext(cb, cq, environment);

			final EnvironmentJoins environmentJoins = new EnvironmentJoins(environment);
			final Join<Environment, Location> location = environmentJoins.getLocation();
			final Join<Location, Region> region = environmentJoins.getLocationJoins().getRegion();
			final Join<Location, District> district = environmentJoins.getLocationJoins().getDistrict();
			final Join<Location, Community> community = environmentJoins.getLocationJoins().getCommunity();

			cq.multiselect(
				environment.get(Environment.UUID),
				environment.get(Environment.EXTERNAL_ID),
				environment.get(Environment.ENVIRONMENT_NAME),
				environment.get(Environment.ENVIRONMENT_MEDIA),
				region.get(Region.NAME),
				district.get(District.NAME),
				community.get(Community.NAME),
				location.get(Location.LATITUDE),
				location.get(Location.LONGITUDE),
				location.get(Location.POSTAL_CODE),
				location.get(Location.CITY),
				environment.get(Environment.REPORT_DATE),
				environment.get(Environment.INVESTIGATION_STATUS),
				JurisdictionHelper.booleanSelector(cb, service.inJurisdictionOrOwned(environmentQueryContext)));

			cq.where(environment.get(Environment.ID).in(batchedIds));
			sortBy(sortProperties, environmentQueryContext);

			environments.addAll(QueryHelper.getResultList(em, cq, null, null));
		});

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		pseudonymizer.pseudonymizeDtoCollection(EnvironmentIndexDto.class, environments, EnvironmentIndexDto::isInJurisdiction, null);

		return environments;
	}

	private List<Long> getIndexListIds(EnvironmentCriteria environmentCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		final Root<Environment> environment = cq.from(Environment.class);

		final EnvironmentQueryContext environmentQueryContext = new EnvironmentQueryContext(cb, cq, environment);

		List<Selection<?>> selections = new ArrayList<>();
		selections.add(environment.get(Environment.ID));
		selections.addAll(sortBy(sortProperties, environmentQueryContext));

		cq.multiselect(selections);

		Predicate filter = CriteriaBuilderHelper
			.and(cb, service.createUserFilter(environmentQueryContext), service.buildCriteriaFilter(environmentCriteria, environmentQueryContext));
		if (filter != null) {
			cq.where(filter);
		}
		cq.distinct(true);

		return QueryHelper.getResultList(em, cq, first, max).stream().map(t -> t.get(0, Long.class)).collect(Collectors.toList());
	}

	private List<Selection<?>> sortBy(List<SortProperty> sortProperties, EnvironmentQueryContext environmentQueryContext) {

		List<Selection<?>> selections = new ArrayList<>();
		CriteriaBuilder cb = environmentQueryContext.getCriteriaBuilder();
		CriteriaQuery<?> cq = environmentQueryContext.getQuery();
		if (sortProperties != null && !sortProperties.isEmpty()) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case EnvironmentIndexDto.UUID:
				case EnvironmentIndexDto.EXTERNAL_ID:
				case EnvironmentIndexDto.ENVIRONMENT_NAME:
				case EnvironmentIndexDto.ENVIRONMENT_MEDIA:
				case EnvironmentIndexDto.INVESTIGATION_STATUS:
				case EnvironmentIndexDto.REPORT_DATE:
					expression = environmentQueryContext.getRoot().get(sortProperty.propertyName);
					break;
				case EnvironmentIndexDto.REGION:
					Join<Location, Region> region = environmentQueryContext.getJoins().getLocationJoins().getRegion();
					expression = region.get(Region.NAME);
					break;
				case EnvironmentIndexDto.DISTRICT:
					Join<Location, District> district = environmentQueryContext.getJoins().getLocationJoins().getDistrict();
					expression = district.get(District.NAME);
					break;
				case EnvironmentIndexDto.COMMUNITY:
					Join<Location, Community> community = environmentQueryContext.getJoins().getLocationJoins().getCommunity();
					expression = community.get(Community.NAME);
					break;
				case EnvironmentIndexDto.POSTAL_CODE:
				case EnvironmentIndexDto.LATITUDE:
				case EnvironmentIndexDto.LONGITUDE:
				case EnvironmentIndexDto.CITY:
					Join<Environment, Location> location = environmentQueryContext.getJoins().getLocation();
					expression = location.get(sortProperty.propertyName);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
				selections.add(expression);
			}
			cq.orderBy(order);
		} else {
			Path<Object> changeDate = environmentQueryContext.getRoot().get(Environment.CHANGE_DATE);
			cq.orderBy(cb.desc(changeDate));
			selections.add(changeDate);
		}

		return selections;
	}

	@Override
	public void validate(EnvironmentDto dto) throws ValidationRuntimeException {
		if (dto.getEnvironmentMedia() != EnvironmentMedia.WATER
			&& (dto.getWaterType() != null || dto.getInfrastructureDetails() != null || MapUtils.isNotEmpty(dto.getWaterUse()))) {
			throw new ValidationRuntimeException(
				I18nProperties.getValidationError(
					Validations.environmentWaterFieldsSetWithNotWaterMedia,
					String.join(
						", ",
						I18nProperties.getPrefixCaption(EnvironmentDto.I18N_PREFIX, EnvironmentDto.WATER_TYPE),
						I18nProperties.getPrefixCaption(EnvironmentDto.I18N_PREFIX, EnvironmentDto.INFRASTUCTURE_DETAILS),
						I18nProperties.getPrefixCaption(EnvironmentDto.I18N_PREFIX, EnvironmentDto.WATER_USE))));
		}

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
	}

	@Override
	protected Environment fillOrBuildEntity(EnvironmentDto source, Environment target, boolean checkChangeDate) {
		boolean targetWasNull = isNull(target);
		target = DtoHelper.fillOrBuildEntity(source, target, Environment::new, checkChangeDate);

		if (targetWasNull) {
			FacadeHelper.setUuidIfDtoExists(target.getLocation(), source.getLocation());
		}

		target.setDescription(source.getDescription());
		target.setEnvironmentMedia(source.getEnvironmentMedia());
		target.setEnvironmentName(source.getEnvironmentName());
		target.setExternalId(source.getExternalId());
		target.setInfrastructureDetails(source.getInfrastructureDetails());
		target.setInvestigationStatus(source.getInvestigationStatus());
		target.setLocation(locationFacade.fillOrBuildEntity(source.getLocation(), target.getLocation(), checkChangeDate));
		target.setOtherInfrastructureDetails(source.getOtherInfrastructureDetails());
		target.setOtherWaterType(source.getOtherWaterType());
		target.setOtherWaterUse(source.getOtherWaterUse());
		target.setReportDate(source.getReportDate());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setResponsibleUser(userService.getByReferenceDto(source.getResponsibleUser()));
		target.setWaterType(source.getWaterType());
		target.setWaterUse(source.getWaterUse());

		target.setDeleted(source.isDeleted());
		target.setDeletionReason(source.getDeletionReason());
		target.setOtherDeletionReason(source.getOtherDeletionReason());
		return target;
	}

	@Override
	protected EnvironmentDto toDto(Environment source) {

		if (source == null) {
			return null;
		}
		EnvironmentDto target = new EnvironmentDto();
		DtoHelper.fillDto(target, source);

		target.setDescription(source.getDescription());
		target.setEnvironmentMedia(source.getEnvironmentMedia());
		target.setEnvironmentName(source.getEnvironmentName());
		target.setExternalId(source.getExternalId());
		target.setInfrastructureDetails(source.getInfrastructureDetails());
		target.setInvestigationStatus(source.getInvestigationStatus());
		target.setLocation(LocationFacadeEjb.toDto(source.getLocation()));
		target.setOtherInfrastructureDetails(source.getOtherInfrastructureDetails());
		target.setOtherWaterType(source.getOtherWaterType());
		target.setOtherWaterUse(source.getOtherWaterUse());
		target.setReportDate(source.getReportDate());
		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setResponsibleUser(UserFacadeEjb.toReferenceDto(source.getResponsibleUser()));
		target.setWaterType(source.getWaterType());
		target.setWaterUse(source.getWaterUse());

		target.setDeleted(source.isDeleted());
		target.setDeletionReason(source.getDeletionReason());
		target.setOtherDeletionReason(source.getOtherDeletionReason());

		return target;
	}

	@Override
	protected EnvironmentReferenceDto toRefDto(Environment environment) {
		return toReferenceDto(environment);
	}

	public static EnvironmentReferenceDto toReferenceDto(Environment entity) {

		if (entity == null) {
			return null;
		}

		return new EnvironmentReferenceDto(entity.getUuid(), entity.getEnvironmentName());
	}

	@Override
	@RightsAllowed(UserRight._ENVIRONMENT_ARCHIVE)
	public ProcessedEntity archive(String entityUuid, Date endOfProcessingDate) {
		return super.archive(entityUuid, endOfProcessingDate);
	}

	@Override
	@RightsAllowed(UserRight._ENVIRONMENT_ARCHIVE)
	public List<ProcessedEntity> dearchive(List<String> entityUuids, String dearchiveReason) {
		return super.dearchive(entityUuids, dearchiveReason);
	}

	@Override
	@RightsAllowed(UserRight._ENVIRONMENT_DELETE)
	public void delete(String uuid, DeletionDetails deletionDetails) throws ExternalSurveillanceToolRuntimeException {
		Environment environment = service.getByUuid(uuid);

		if (!service.inJurisdictionOrOwned(environment)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.messageEventParticipantOutsideJurisdictionDeletionDenied));
		}

		service.delete(environment, deletionDetails);
	}

	@Override
	@RightsAllowed(UserRight._ENVIRONMENT_DELETE)
	public void restore(String uuid) {
		super.restore(uuid);
	}

	@Override
	protected void pseudonymizeDto(Environment source, EnvironmentDto dto, Pseudonymizer pseudonymizer, boolean inJurisdiction) {
		if (dto != null) {
			pseudonymizer.pseudonymizeDto(EnvironmentDto.class, dto, inJurisdiction, e -> {
				pseudonymizer.pseudonymizeUser(source.getReportingUser(), userService.getCurrentUser(), dto::setReportingUser);
			});
		}
	}

	@Override
	protected void restorePseudonymizedDto(EnvironmentDto dto, EnvironmentDto existingDto, Environment entity, Pseudonymizer pseudonymizer) {
		if (existingDto != null) {
			boolean inJurisdiction = service.inJurisdictionOrOwned(entity);
			pseudonymizer.restorePseudonymizedValues(EnvironmentDto.class, dto, existingDto, inJurisdiction);
			pseudonymizer.restoreUser(entity.getReportingUser(), userService.getCurrentUser(), dto, dto::setReportingUser);
		}
	}

	@Override
	protected DeletableEntityType getDeletableEntityType() {
		return null;
	}

	@Override
	public List<String> getArchivedUuidsSince(Date since) {
		return null;
	}

	@RightsAllowed(UserRight._ENVIRONMENT_DELETE)
	@Override
	public List<ProcessedEntity> delete(List<String> uuids, DeletionDetails deletionDetails) {
		throw new NotImplementedException();
	}

	@Override
	@RightsAllowed(UserRight._ENVIRONMENT_DELETE)
	public List<ProcessedEntity> restore(List<String> uuids) {
		throw new NotImplementedException();
	}

	@Override
	public List<String> getAllActiveUuids() {
		User user = userService.getCurrentUser();

		if (user == null) {
			return Collections.emptyList();
		}

		return service.getAllActiveUuids(user);
	}

	@LocalBean
	@Stateless
	public static class EnvironmentFacadeEjbLocal extends EnvironmentFacadeEjb {

		public EnvironmentFacadeEjbLocal() {
		}

		@Inject
		public EnvironmentFacadeEjbLocal(EnvironmentService service) {
			super(service);
		}

	}
}
