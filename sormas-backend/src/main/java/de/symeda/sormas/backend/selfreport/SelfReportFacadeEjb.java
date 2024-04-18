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

import java.util.ArrayList;
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
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.NotImplementedException;

import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.selfreport.SelfReportCriteria;
import de.symeda.sormas.api.selfreport.SelfReportDto;
import de.symeda.sormas.api.selfreport.SelfReportFacade;
import de.symeda.sormas.api.selfreport.SelfReportIndexDto;
import de.symeda.sormas.api.selfreport.SelfReportReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.common.AbstractCoreFacadeEjb;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "SelfReportFacade")
@RightsAllowed(UserRight._SELF_REPORT_VIEW)
public class SelfReportFacadeEjb
	extends AbstractCoreFacadeEjb<SelfReport, SelfReportDto, SelfReportIndexDto, SelfReportReferenceDto, SelfReportService, SelfReportCriteria>
	implements SelfReportFacade {

	@EJB
	private LocationFacadeEjbLocal locationFacade;

	public SelfReportFacadeEjb() {
	}

	@Inject
	public SelfReportFacadeEjb(SelfReportService service) {
		super(SelfReport.class, SelfReportDto.class, service);
	}

	@Override
	@RightsAllowed({
		UserRight._SELF_REPORT_CREATE,
		UserRight._SELF_REPORT_EDIT })
	public SelfReportDto save(@Valid @NotNull SelfReportDto dto) {
		SelfReport existingSelfReport = dto.getUuid() != null ? service.getByUuid(dto.getUuid()) : null;

		FacadeHelper.checkCreateAndEditRights(existingSelfReport, userService, UserRight.SELF_REPORT_CREATE, UserRight.SELF_REPORT_EDIT);

		SelfReportDto existingDto = toDto(existingSelfReport);
		Pseudonymizer<SelfReportDto> pseudonymizer = createPseudonymizer(existingSelfReport);
		restorePseudonymizedDto(dto, existingDto, existingSelfReport, pseudonymizer);

		validate(dto);

		SelfReport selfReport = fillOrBuildEntity(dto, existingSelfReport, true);
		service.ensurePersisted(selfReport);

		return toPseudonymizedDto(selfReport, pseudonymizer);
	}

	@Override
	public long count(SelfReportCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<SelfReport> selfReport = cq.from(SelfReport.class);

		final SelfReportQueryContext selfReportQueryContext = new SelfReportQueryContext(cb, cq, selfReport);

		Predicate filter = CriteriaBuilderHelper
			.and(cb, service.createUserFilter(selfReportQueryContext), service.buildCriteriaFilter(criteria, selfReportQueryContext));
		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(selfReport));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public List<SelfReportIndexDto> getIndexList(SelfReportCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		List<Long> indexListIds = getIndexListIds(criteria, first, max, sortProperties);

		List<SelfReportIndexDto> selfReports = new ArrayList<>();

		IterableHelper.executeBatched(indexListIds, ModelConstants.PARAMETER_LIMIT, batchedIds -> {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<SelfReport> selfReport = cq.from(SelfReport.class);

			final SelfReportQueryContext selfReportQueryContext = new SelfReportQueryContext(cb, cq, selfReport);

			final SelfReportJoins selfReportJoins = selfReportQueryContext.getJoins();
			final Join<SelfReport, Location> location = selfReportJoins.getAddress();
			final Join<Location, District> district = selfReportJoins.getAddressJoins().getDistrict();
			Join<SelfReport, User> responsibleUser = selfReportJoins.getResponsibleUser();

			cq.multiselect(
				selfReport.get(SelfReport.UUID),
				selfReport.get(SelfReport.TYPE),
				selfReport.get(SelfReport.REPORT_DATE),
				selfReport.get(SelfReport.DISEASE),
				selfReport.get(SelfReport.FIRST_NAME),
				selfReport.get(SelfReport.LAST_NAME),
				cb.construct(
					AgeAndBirthDateDto.class,
					selfReport.get(SelfReport.BIRTHDATE_DD),
					selfReport.get(SelfReport.BIRTHDATE_MM),
					selfReport.get(SelfReport.BIRTHDATE_YYYY)),
				selfReport.get(SelfReport.SEX),
				district.get(District.NAME),
				location.get(Location.STREET),
				location.get(Location.HOUSE_NUMBER),
				location.get(Location.POSTAL_CODE),
				location.get(Location.CITY),
				selfReport.get(SelfReport.EMAIL),
				selfReport.get(SelfReport.PHONE_NUMBER),
				cb.construct(
					UserReferenceDto.class,
					responsibleUser.get(User.UUID),
					responsibleUser.get(User.FIRST_NAME),
					responsibleUser.get(User.LAST_NAME)),
				selfReport.get(SelfReport.INVESTIGATION_STATUS),
				selfReport.get(SelfReport.PROCESSING_STATUS),
				selfReport.get(SelfReport.DELETION_REASON),
				selfReport.get(SelfReport.OTHER_DELETION_REASON));

			cq.where(selfReport.get(SelfReport.ID).in(batchedIds));
			sortBy(sortProperties, selfReportQueryContext);

			selfReports.addAll(QueryHelper.getResultList(em, cq, new SelfReportIndexDtoResultTransformer(), null, null));
		});

		Pseudonymizer<SelfReportIndexDto> pseudonymizer = createGenericPlaceholderPseudonymizer();
		pseudonymizer.pseudonymizeDtoCollection(SelfReportIndexDto.class, selfReports, SelfReportIndexDto::isInJurisdiction, null);

		return selfReports;
	}

	private List<Long> getIndexListIds(SelfReportCriteria selfReportCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		final Root<SelfReport> selfReport = cq.from(SelfReport.class);

		final SelfReportQueryContext selfReportQueryContext = new SelfReportQueryContext(cb, cq, selfReport);

		List<Selection<?>> selections = new ArrayList<>();
		selections.add(selfReport.get(SelfReport.ID));
		selections.addAll(sortBy(sortProperties, selfReportQueryContext));

		cq.multiselect(selections);

		Predicate filter = CriteriaBuilderHelper
			.and(cb, service.createUserFilter(selfReportQueryContext), service.buildCriteriaFilter(selfReportCriteria, selfReportQueryContext));
		if (filter != null) {
			cq.where(filter);
		}
		cq.distinct(true);

		return QueryHelper.getResultList(em, cq, first, max).stream().map(t -> t.get(0, Long.class)).collect(Collectors.toList());
	}

	private List<Selection<?>> sortBy(List<SortProperty> sortProperties, SelfReportQueryContext selfReportQueryContext) {

		List<Selection<?>> selections = new ArrayList<>();
		CriteriaBuilder cb = selfReportQueryContext.getCriteriaBuilder();
		CriteriaQuery<?> cq = selfReportQueryContext.getQuery();
		if (sortProperties != null && !sortProperties.isEmpty()) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case SelfReportIndexDto.UUID:
				case SelfReportIndexDto.TYPE:
				case SelfReportIndexDto.REPORT_DATE:
				case SelfReportIndexDto.DISEASE:
				case SelfReportIndexDto.FIRST_NAME:
				case SelfReportIndexDto.LAST_NAME:
				case SelfReportIndexDto.SEX:
				case SelfReportIndexDto.EMAIL:
				case SelfReportIndexDto.PHONE_NUMBER:
				case SelfReportIndexDto.INVESTIGATION_STATUS:
				case SelfReportIndexDto.PROCESSING_STATUS:
					expression = selfReportQueryContext.getRoot().get(sortProperty.propertyName);
					break;
				case SelfReportIndexDto.DISTRICT:
					Join<Location, District> district = selfReportQueryContext.getJoins().getAddressJoins().getDistrict();
					expression = district.get(District.NAME);
					break;
				case SelfReportIndexDto.AGE_AND_BIRTH_DATE:
					break;
				case SelfReportDto.ADDRESS:
					Join<SelfReport, Location> location = selfReportQueryContext.getJoins().getAddress();
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
			Path<Object> changeDate = selfReportQueryContext.getRoot().get(SelfReport.CHANGE_DATE);
			cq.orderBy(cb.desc(changeDate));
			selections.add(changeDate);
		}

		return selections;
	}

	@Override
	public void validate(SelfReportDto dto) throws ValidationRuntimeException {

	}

	@Override
	public List<String> getArchivedUuidsSince(Date since) {
		throw new NotImplementedException();
	}

	@Override
	@RightsAllowed(UserRight._SELF_REPORT_DELETE)
	public List<ProcessedEntity> delete(List<String> uuids, DeletionDetails deletionDetails) {
		return null;
	}

	@Override
	@RightsAllowed(UserRight._SELF_REPORT_DELETE)
	public List<ProcessedEntity> restore(List<String> uuids) {
		return null;
	}

	@Override
	protected SelfReport fillOrBuildEntity(SelfReportDto source, SelfReport target, boolean checkChangeDate) {
		target = DtoHelper.fillOrBuildEntity(source, target, SelfReport::new, checkChangeDate);

		target.setType(source.getType());
		target.setReportDate(source.getReportDate());
		target.setCaseReference(source.getCaseReference());
		target.setDisease(source.getDisease());
		target.setDiseaseVariant(source.getDiseaseVariant());
		target.setFirstName(source.getFirstName());
		target.setLastName(source.getLastName());
		target.setSex(source.getSex());
		target.setBirthdateDD(source.getBirthdateDD());
		target.setBirthdateMM(source.getBirthdateMM());
		target.setBirthdateYYYY(source.getBirthdateYYYY());
		target.setNationalHealthId(source.getNationalHealthId());
		target.setEmail(source.getEmail());
		target.setPhoneNumber(source.getPhoneNumber());
		target.setAddress(locationFacade.fillOrBuildEntity(source.getAddress(), target.getAddress(), checkChangeDate));
		target.setComment(source.getComment());
		target.setResponsibleUser(userService.getByReferenceDto(source.getResponsibleUser()));
		target.setInvestigationStatus(source.getInvestigationStatus());
		target.setProcessingStatus(source.getProcessingStatus());

		return target;
	}

	@Override
	protected SelfReportDto toDto(SelfReport source) {
		if (source == null) {
			return null;
		}
		SelfReportDto target = new SelfReportDto();

		DtoHelper.fillDto(target, source);

		target.setType(source.getType());
		target.setReportDate(source.getReportDate());
		target.setCaseReference(source.getCaseReference());
		target.setDisease(source.getDisease());
		target.setDiseaseVariant(source.getDiseaseVariant());
		target.setFirstName(source.getFirstName());
		target.setLastName(source.getLastName());
		target.setSex(source.getSex());
		target.setBirthdateDD(source.getBirthdateDD());
		target.setBirthdateMM(source.getBirthdateMM());
		target.setBirthdateYYYY(source.getBirthdateYYYY());
		target.setNationalHealthId(source.getNationalHealthId());
		target.setEmail(source.getEmail());
		target.setPhoneNumber(source.getPhoneNumber());
		target.setAddress(LocationFacadeEjb.toDto(source.getAddress()));
		target.setComment(source.getComment());
		target.setResponsibleUser(UserFacadeEjb.toReferenceDto(source.getResponsibleUser()));
		target.setInvestigationStatus(source.getInvestigationStatus());
		target.setProcessingStatus(source.getProcessingStatus());

		return target;
	}

	@Override
	protected SelfReportReferenceDto toRefDto(SelfReport selfReport) {
		return new SelfReportReferenceDto(selfReport.getUuid());
	}

	@Override
	protected void pseudonymizeDto(SelfReport source, SelfReportDto dto, Pseudonymizer<SelfReportDto> pseudonymizer, boolean inJurisdiction) {

	}

	@Override
	protected void restorePseudonymizedDto(
		SelfReportDto dto,
		SelfReportDto existingDto,
		SelfReport entity,
		Pseudonymizer<SelfReportDto> pseudonymizer) {

	}

	@Override
	protected DeletableEntityType getDeletableEntityType() {
		return DeletableEntityType.SELF_REPORT;
	}

	@LocalBean
	@Stateless
	public static class SelfReportFacadeEjbLocal extends SelfReportFacadeEjb {

		public SelfReportFacadeEjbLocal() {
		}

		@Inject
		public SelfReportFacadeEjbLocal(SelfReportService service) {
			super(service);
		}
	}
}
