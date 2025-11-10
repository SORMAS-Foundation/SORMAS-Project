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

package de.symeda.sormas.backend.epipulse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.epipulse.EpipulseExportCriteria;
import de.symeda.sormas.api.epipulse.EpipulseExportDto;
import de.symeda.sormas.api.epipulse.EpipulseExportFacade;
import de.symeda.sormas.api.epipulse.EpipulseExportIndexDto;
import de.symeda.sormas.api.epipulse.EpipulseExportReferenceDto;
import de.symeda.sormas.api.epipulse.EpipulseExportStatus;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "EpipulseExportFacade")
@RightsAllowed(UserRight._EPIPULSE_EXPORT_VIEW)
public class EpipulseExportFacadeEjb implements EpipulseExportFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public static final String TOTAL_RECORDS_FORMATTED = "totalRecordsFormatted";
	public static final String EXPORT_FILE_SIZE_FORMATTED = "exportFileSizeFormatted";

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private EpipulseExportService epipulseExportService;

	@EJB
	private UserService userService;

	@EJB
	private EpipulseExportTimerEjb exportTimerEjb;

	@RightsAllowed({
		UserRight._EPIPULSE_EXPORT_CREATE })
	@Override
	public EpipulseExportDto saveEpipulseExport(EpipulseExportDto dto) {
		EpipulseExport existingEpipulseExport = epipulseExportService.getByUuid(dto.getUuid());

		FacadeHelper
			.checkCreateAndEditRights(existingEpipulseExport, userService, UserRight.EPIPULSE_EXPORT_CREATE, UserRight.EPIPULSE_EXPORT_CREATE);

		validate(dto);

		EpipulseExport epipulseExport = fillOrBuildEntity(dto, existingEpipulseExport);

		epipulseExportService.ensurePersisted(epipulseExport);

		exportTimerEjb.scheduleExportDisease(epipulseExport.getUuid(), epipulseExport.getSubjectCode());

		return toDto(epipulseExport);
	}

	@Override
	public Page<EpipulseExportIndexDto> getIndexPage(
		EpipulseExportCriteria criteria,
		Integer offset,
		Integer size,
		List<SortProperty> sortProperties) {
		List<EpipulseExportIndexDto> epipulseExportIndexDtoList = getIndexList(criteria, offset, size, sortProperties);
		long totalElementCount = count(criteria);
		return new Page<EpipulseExportIndexDto>(epipulseExportIndexDtoList, offset, size, totalElementCount);
	}

	@Override
	public long count(EpipulseExportCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<EpipulseExport> epipulseExport = cq.from(EpipulseExport.class);
		EpipulseExportQueryContext queryContext = new EpipulseExportQueryContext(cb, cq, epipulseExport);
		EpipulseExportJoins joins = queryContext.getJoins();

		Predicate filter = epipulseExportService.buildCriteriaFilter(criteria, queryContext);

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(epipulseExport));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public List<EpipulseExportIndexDto> getIndexList(EpipulseExportCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		List<Long> indexListIds = getIndexListIds(criteria, first, max, sortProperties);

		List<EpipulseExportIndexDto> epipulseExports = new ArrayList<>();

		IterableHelper.executeBatched(indexListIds, ModelConstants.PARAMETER_LIMIT, batchedIds -> {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<EpipulseExport> epipulseExport = cq.from(EpipulseExport.class);

			EpipulseExportQueryContext queryContext = new EpipulseExportQueryContext(cb, cq, epipulseExport);
			EpipulseExportJoins joins = queryContext.getJoins();

			List<Selection<?>> selections = new ArrayList<>(
				Arrays.asList(
					epipulseExport.get(EpipulseExport.UUID),
					epipulseExport.get(EpipulseExport.SUBJECT_CODE),
					epipulseExport.get(EpipulseExport.START_DATE),
					epipulseExport.get(EpipulseExport.END_DATE),
					epipulseExport.get(EpipulseExport.STATUS),
					epipulseExport.get(EpipulseExport.STATUS_CHANGE_DATE),
					epipulseExport.get(EpipulseExport.TOTAL_RECORDS),
					epipulseExport.get(EpipulseExport.EXPORT_FILE_NAME),
					epipulseExport.get(EpipulseExport.EXPORT_FILE_SIZE),
					epipulseExport.get(EpipulseExport.CREATION_DATE),
					joins.getCreationUser().get(User.UUID),
					joins.getCreationUser().get(User.FIRST_NAME),
					joins.getCreationUser().get(User.LAST_NAME)));

			List<Order> orderList = getOrderList(sortProperties, queryContext);
			selections.addAll(orderList.stream().map(Order::getExpression).collect(Collectors.toList()));

			cq.multiselect(selections);

			cq.where(epipulseExport.get(EpipulseExport.ID).in(batchedIds));
			cq.orderBy(orderList);
			cq.distinct(true);

			epipulseExports.addAll(QueryHelper.getResultList(em, cq, new EpipulseExportIndexDtoResultTransformer(), null, null));
		});

		return epipulseExports;
	}

	private List<Long> getIndexListIds(EpipulseExportCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<EpipulseExport> epipulseExport = cq.from(EpipulseExport.class);

		EpipulseExportQueryContext queryContext = new EpipulseExportQueryContext(cb, cq, epipulseExport);

		List<Selection<?>> selections = new ArrayList<>();
		selections.add(epipulseExport.get(EpipulseExport.ID));

		List<Order> orderList = getOrderList(sortProperties, queryContext);
		List<Expression<?>> sortColumns = orderList.stream().map(Order::getExpression).collect(Collectors.toList());
		selections.addAll(sortColumns);

		cq.multiselect(selections);

		Predicate filter = epipulseExportService.buildCriteriaFilter(criteria, queryContext);

		if (filter != null) {
			cq.where(filter);
		}

		cq.distinct(true);
		cq.orderBy(orderList);

		return QueryHelper.getResultList(em, cq, first, max).stream().map(t -> t.get(0, Long.class)).collect(Collectors.toList());
	}

	private List<Order> getOrderList(List<SortProperty> sortProperties, EpipulseExportQueryContext queryContext) {
		CriteriaBuilder cb = queryContext.getCriteriaBuilder();
		From<?, EpipulseExport> epipulseExport = queryContext.getRoot();
		EpipulseExportJoins joins = queryContext.getJoins();

		List<Order> orderList = new ArrayList<>();
		if (sortProperties != null && sortProperties.size() > 0) {
			for (SortProperty sortProperty : sortProperties) {
				CriteriaBuilderHelper.OrderBuilder orderBuilder = CriteriaBuilderHelper.createOrderBuilder(cb, sortProperty.ascending);
				final List<Order> order;

				switch (sortProperty.propertyName) {
				case EpipulseExportIndexDto.UUID:
				case EpipulseExportIndexDto.SUBJECT_CODE:
				case EpipulseExportIndexDto.START_DATE:
				case EpipulseExportIndexDto.END_DATE:
				case EpipulseExportIndexDto.STATUS:
				case EpipulseExportIndexDto.STATUS_CHANGE_DATE:
				case EpipulseExportIndexDto.TOTAL_RECORDS:
				case EpipulseExportIndexDto.EXPORT_FILE_SIZE:
				case EpipulseExportIndexDto.CREATION_DATE:
				case TOTAL_RECORDS_FORMATTED:
					order = orderBuilder.build(epipulseExport.get(EpipulseExportIndexDto.TOTAL_RECORDS));
					break;
				case EXPORT_FILE_SIZE_FORMATTED:
					order = orderBuilder.build(epipulseExport.get(EpipulseExportIndexDto.EXPORT_FILE_SIZE));
					break;
				case EpipulseExportIndexDto.CREATION_USER:
					order = orderBuilder
						.build(cb.lower(joins.getCreationUser().get(User.LAST_NAME)), cb.lower(joins.getCreationUser().get(User.FIRST_NAME)));
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}

				orderList.addAll(order);
			}
		}

		if (orderList.isEmpty()) {
			orderList.add(cb.desc(epipulseExport.get(EpipulseExportIndexDto.CREATION_DATE)));
		}

		return orderList;
	}

	@Override
	public EpipulseExportDto getEpiPulseExportByUuid(String uuid) {
		if (epipulseExportService.isArchived(uuid)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorAccessDenied));
		}

		EpipulseExport epipulseExport = epipulseExportService.getByUuid(uuid);

		return toDto(epipulseExport);
	}

	public EpipulseExport fillOrBuildEntity(EpipulseExportDto source, EpipulseExport target) {
		if (source == null) {
			return null;
		}

		target = DtoHelper.fillOrBuildEntity(source, target, EpipulseExport::new, true);

		target.setSubjectCode(source.getSubjectCode());
		target.setStartDate(source.getStartDate());
		target.setEndDate(source.getEndDate());
		target.setStatus(source.getStatus());
		target.setStatusChangeDate(source.getStatusChangeDate());
		target.setCreationUser(userService.getByReferenceDto(source.getCreationUser()));

		return target;
	}

	private EpipulseExportDto toDto(EpipulseExport entity) {
		return toEpipulseExportDto(entity);
	}

	public EpipulseExportDto toEpipulseExportDto(EpipulseExport entity) {

		if (entity == null) {
			return null;
		}
		EpipulseExportDto dto = new EpipulseExportDto();
		DtoHelper.fillDto(dto, entity);

		dto.setSubjectCode(entity.getSubjectCode());
		dto.setStartDate(entity.getStartDate());
		dto.setEndDate(entity.getEndDate());
		dto.setStatus(entity.getStatus());
		dto.setStatusChangeDate(entity.getStatusChangeDate());
		dto.setTotalRecords(entity.getTotalRecords());
		dto.setExportFileName(entity.getExportFileName());
		dto.setExportFileSize(entity.getExportFileSize());
		dto.setCreationUser(UserFacadeEjb.toReferenceDto(entity.getCreationUser()));

		return dto;
	}

	protected EpipulseExportReferenceDto toRefDto(EpipulseExport aefi) {
		return toReferenceDto(aefi);
	}

	public EpipulseExportReferenceDto toReferenceDto(EpipulseExport entity) {

		if (entity == null) {
			return null;
		}

		return new EpipulseExportReferenceDto(entity.getUuid());
	}

	private void validate(EpipulseExportDto epipulseExportDto) throws ValidationRuntimeException {
		if (DateHelper.isDateAfter(epipulseExportDto.getStartDate(), epipulseExportDto.getEndDate())) {
			String validationError = String.format(
				I18nProperties.getValidationError(Validations.afterDate),
				I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, EpipulseExportDto.START_DATE),
				I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, EpipulseExportDto.END_DATE));
			throw new ValidationRuntimeException(validationError);
		}
	}

	@RightsAllowed({
		UserRight._EPIPULSE_EXPORT_CREATE })
	@Override
	public void cancelEpipulseExport(String uuid) {
		EpipulseExport existingEpipulseExport = epipulseExportService.getByUuid(uuid);

		if (existingEpipulseExport.getStatus() == EpipulseExportStatus.COMPLETED
			|| existingEpipulseExport.getStatus() == EpipulseExportStatus.FAILED
			|| existingEpipulseExport.getStatus() == EpipulseExportStatus.CANCELLED) {
			throw new ValidationRuntimeException(I18nProperties.getString(Strings.messageEpipulseExportNoCancel));
		}

		FacadeHelper
			.checkCreateAndEditRights(existingEpipulseExport, userService, UserRight.EPIPULSE_EXPORT_CREATE, UserRight.EPIPULSE_EXPORT_CREATE);

		existingEpipulseExport.setStatus(EpipulseExportStatus.CANCELLED);
		existingEpipulseExport.setStatusChangeDate(new Date());

		epipulseExportService.ensurePersisted(existingEpipulseExport);
	}

	@RightsAllowed({
		UserRight._EPIPULSE_EXPORT_CREATE })
	@Override
	public void deleteEpipulseExport(String uuid) {
		EpipulseExport existingEpipulseExport = epipulseExportService.getByUuid(uuid);

		if (existingEpipulseExport.getStatus() == EpipulseExportStatus.PENDING
			|| existingEpipulseExport.getStatus() == EpipulseExportStatus.IN_PROGRESS) {
			throw new ValidationRuntimeException(I18nProperties.getString(Strings.messageEpipulseExportNoDelete));
		}

		FacadeHelper
			.checkCreateAndEditRights(existingEpipulseExport, userService, UserRight.EPIPULSE_EXPORT_CREATE, UserRight.EPIPULSE_EXPORT_CREATE);

		existingEpipulseExport.setDeleted(true);
		existingEpipulseExport.setDeletionReason(DeletionReason.OTHER_REASON);

		epipulseExportService.ensurePersisted(existingEpipulseExport);
	}

	@Override
	public void delete(String uuid, DeletionDetails deletionDetails) {
	}

	@Override
	public List<ProcessedEntity> delete(List<String> uuids, DeletionDetails deletionDetails) {
		return List.of();
	}

	@Override
	public void restore(String uuid) {

	}

	@Override
	public List<ProcessedEntity> restore(List<String> uuids) {
		return List.of();
	}

	@Override
	public boolean isDeleted(String uuid) {
		return false;
	}

	@LocalBean
	@Stateless
	public static class EpipulseExportFacadeEjbLocal extends EpipulseExportFacadeEjb {

	}
}
