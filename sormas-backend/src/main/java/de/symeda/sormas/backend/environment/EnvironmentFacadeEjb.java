
package de.symeda.sormas.backend.environment;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.Arrays;
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

import org.apache.commons.collections4.CollectionUtils;

import de.symeda.sormas.api.common.CoreEntityType;
import de.symeda.sormas.api.environment.EnvironmentCriteria;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentFacade;
import de.symeda.sormas.api.environment.EnvironmentIndexDto;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.common.AbstractCoreFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless(name = "EnvironmentFacade")
public class EnvironmentFacadeEjb
	extends AbstractCoreFacadeEjb<Environment, EnvironmentDto, EnvironmentIndexDto, EnvironmentReferenceDto, EnvironmentService, EnvironmentCriteria>
	implements EnvironmentFacade {

	public EnvironmentFacadeEjb() {
	}

	@Inject
	public EnvironmentFacadeEjb(EnvironmentService service) {
		super(Environment.class, EnvironmentDto.class, service);
	}

	public static final List<String> VALID_SORT_PROPERTY_NAMES = Arrays.asList(
		EnvironmentIndexDto.UUID,
		EnvironmentIndexDto.ENVIRONMENT_MEDIA,
		EnvironmentIndexDto.ENVIRONMENT_NAME,
		EnvironmentIndexDto.GPS_LAT,
		EnvironmentIndexDto.GPS_LON,
		EnvironmentIndexDto.REGION,
		EnvironmentIndexDto.DISTRICT,
		EnvironmentIndexDto.COMMUNITY,
		EnvironmentIndexDto.POSTAL_CODE,
		EnvironmentIndexDto.CITY,
		EnvironmentIndexDto.INVESTIGATION_STATUS,
		EnvironmentIndexDto.REPORT_DATE);

	@EJB
	private LocationFacadeEjb.LocationFacadeEjbLocal locationFacade;

	@Override
	public EnvironmentDto save(EnvironmentDto dto) {
		return save(dto, true);
	}

	public EnvironmentDto save(EnvironmentDto dto, boolean checkChangeDate) {

		Environment existingEnvironment = dto.getUuid() != null ? service.getByUuid(dto.getUuid()) : null;

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
				environment.get(Environment.INVESTIGATION_STATUS));

			cq.where(environment.get(Environment.ID).in(batchedIds));
			cq.orderBy(getOrderList(sortProperties, cb, environment));
			cq.distinct(true);

			environments.addAll(QueryHelper.getResultList(em, cq, null, null));
		});

		return environments;
	}

	private List<Order> getOrderList(List<SortProperty> sortProperties, CriteriaBuilder cb, Root<Environment> environmentRoot) {
		List<Order> order = new ArrayList<>();

		if (!CollectionUtils.isEmpty(sortProperties)) {
			for (SortProperty sortProperty : sortProperties) {
				if (VALID_SORT_PROPERTY_NAMES.contains(sortProperty.propertyName)) {
					Expression<?> expression = environmentRoot.get(sortProperty.propertyName);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
				}
			}
		}

		order.add(cb.desc(environmentRoot.get(Environment.REPORT_DATE)));

		return order;
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

		Predicate filter = service.buildCriteriaFilter(environmentCriteria, environmentQueryContext);

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
				case EnvironmentIndexDto.ENVIRONMENT_NAME:
				case EnvironmentIndexDto.ENVIRONMENT_MEDIA:
				case EnvironmentIndexDto.GPS_LAT:
				case EnvironmentIndexDto.GPS_LON:
				case EnvironmentIndexDto.INVESTIGATION_STATUS:
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
	protected void pseudonymizeDto(Environment source, EnvironmentDto dto, Pseudonymizer pseudonymizer, boolean inJurisdiction) {

	}

	@Override
	protected void restorePseudonymizedDto(EnvironmentDto dto, EnvironmentDto existingDto, Environment entity, Pseudonymizer pseudonymizer) {

	}

	@Override
	protected CoreEntityType getCoreEntityType() {
		return null;
	}

	@Override
	public List<EnvironmentDto> getAllEnvironmentsAfter(Date date) {
		return service.getAllAfter(date).stream().map(r -> toDto(r)).collect(Collectors.toList());
	}

	@Override
	public List<String> getArchivedUuidsSince(Date since) {
		return null;
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
