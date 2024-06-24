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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.selfreport.SelfReportListEntryDto;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.selfreport.SelfReportCriteria;
import de.symeda.sormas.api.selfreport.SelfReportDto;
import de.symeda.sormas.api.selfreport.SelfReportExportDto;
import de.symeda.sormas.api.selfreport.SelfReportFacade;
import de.symeda.sormas.api.selfreport.SelfReportIndexDto;
import de.symeda.sormas.api.selfreport.SelfReportProcessingStatus;
import de.symeda.sormas.api.selfreport.SelfReportReferenceDto;
import de.symeda.sormas.api.selfreport.SelfReportType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractCoreFacadeEjb;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
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
	@EJB
	private CaseService caseService;
	@EJB
	private ContactService contactService;

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
			List<Order> orderList = getOrderList(sortProperties, selfReportQueryContext);

			cq.multiselect(
				Stream
					.concat(
						Stream.of(
							selfReport.get(SelfReport.UUID),
							selfReport.get(SelfReport.TYPE),
							selfReport.get(SelfReport.REPORT_DATE),
							selfReport.get(SelfReport.DISEASE),
							selfReport.get(SelfReport.FIRST_NAME),
							selfReport.get(SelfReport.LAST_NAME),
							selfReport.get(SelfReport.BIRTHDATE_DD),
							selfReport.get(SelfReport.BIRTHDATE_MM),
							selfReport.get(SelfReport.BIRTHDATE_YYYY),
							selfReport.get(SelfReport.SEX),
							district.get(District.NAME),
							location.get(Location.STREET),
							location.get(Location.HOUSE_NUMBER),
							location.get(Location.POSTAL_CODE),
							location.get(Location.CITY),
							selfReport.get(SelfReport.EMAIL),
							selfReport.get(SelfReport.PHONE_NUMBER),
							responsibleUser.get(User.UUID),
							responsibleUser.get(User.FIRST_NAME),
							responsibleUser.get(User.LAST_NAME),
							selfReport.get(SelfReport.INVESTIGATION_STATUS),
							selfReport.get(SelfReport.PROCESSING_STATUS),
							selfReport.get(SelfReport.DELETION_REASON),
							selfReport.get(SelfReport.OTHER_DELETION_REASON)),
						orderList.stream().map(Order::getExpression))
					.collect(Collectors.toList()));

			cq.where(selfReport.get(SelfReport.ID).in(batchedIds));
			cq.orderBy(orderList);

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
		List<Order> orderList = getOrderList(sortProperties, selfReportQueryContext);
		selections.addAll(orderList.stream().map(Order::getExpression).collect(Collectors.toList()));

		cq.multiselect(selections);

		Predicate filter = CriteriaBuilderHelper
			.and(cb, service.createUserFilter(selfReportQueryContext), service.buildCriteriaFilter(selfReportCriteria, selfReportQueryContext));
		if (filter != null) {
			cq.where(filter);
		}
		cq.distinct(true);
		cq.orderBy(orderList);

		return QueryHelper.getResultList(em, cq, first, max).stream().map(t -> t.get(0, Long.class)).collect(Collectors.toList());
	}

	@Override
	@RightsAllowed(UserRight._SELF_REPORT_EXPORT)
	public List<SelfReportExportDto> getExportList(SelfReportCriteria selfReportCriteria, Collection<String> selectedRows, int first, int max) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		final Root<SelfReport> selfReport = cq.from(SelfReport.class);

		final SelfReportQueryContext selfReportQueryContext = new SelfReportQueryContext(cb, cq, selfReport);
		final SelfReportJoins selfReportJoins = selfReportQueryContext.getJoins();
		final Join<SelfReport, Location> location = selfReportJoins.getAddress();
		final Join<Location, District> district = selfReportJoins.getAddressJoins().getDistrict();
		Join<SelfReport, User> responsibleUser = selfReportJoins.getResponsibleUser();

		List<SelfReportExportDto> selfReports = new ArrayList<>();

		cq.multiselect(
			selfReport.get(SelfReport.UUID),
			selfReport.get(SelfReport.TYPE),
			selfReport.get(SelfReport.REPORT_DATE),
			selfReport.get(SelfReport.CASE_REFERENCE),
			selfReport.get(SelfReport.DISEASE),
			selfReport.get(SelfReport.DISEASE_DETAILS),
			selfReport.get(SelfReport.DISEASE_VARIANT),
			selfReport.get(SelfReport.DISEASE_VARIANT_DETAILS),
			selfReport.get(SelfReport.FIRST_NAME),
			selfReport.get(SelfReport.LAST_NAME),
			selfReport.get(SelfReport.SEX),
			location.get(Location.STREET),
			location.get(Location.HOUSE_NUMBER),
			location.get(Location.POSTAL_CODE),
			location.get(Location.CITY),
			selfReport.get(SelfReport.BIRTHDATE_DD),
			selfReport.get(SelfReport.BIRTHDATE_MM),
			selfReport.get(SelfReport.BIRTHDATE_YYYY),
			selfReport.get(SelfReport.NATIONAL_HEALTH_ID),
			selfReport.get(SelfReport.EMAIL),
			selfReport.get(SelfReport.PHONE_NUMBER),
			selfReport.get(SelfReport.DATE_OF_TEST),
			selfReport.get(SelfReport.DATE_OF_SYMPTOMS),
			selfReport.get(SelfReport.WORKPLACE),
			selfReport.get(SelfReport.DATE_WORKPLACE),
			selfReport.get(SelfReport.ISOLATION_DATE),
			selfReport.get(SelfReport.CONTACT_DATE),
			selfReport.get(SelfReport.COMMENT),
			responsibleUser.get(User.UUID),
			responsibleUser.get(User.FIRST_NAME),
			responsibleUser.get(User.LAST_NAME),
			selfReport.get(SelfReport.INVESTIGATION_STATUS),
			selfReport.get(SelfReport.PROCESSING_STATUS),
			selfReport.get(SelfReport.DELETION_REASON),
			selfReport.get(SelfReport.OTHER_DELETION_REASON));

		Predicate filter = service.buildCriteriaFilter(selfReportCriteria, selfReportQueryContext);

		filter = CriteriaBuilderHelper.andInValues(selectedRows, filter, cb, selfReport.get(SelfReport.UUID));
		cq.where(filter);
		cq.orderBy(cb.desc(selfReport.get(SelfReport.REPORT_DATE)), cb.desc(selfReport.get(SelfReport.ID)));

		selfReports.addAll(QueryHelper.getResultList(em, cq, new SelfReportExportDtoResultTransformer(), null, null));

		return selfReports;
	}

	private List<Order> getOrderList(List<SortProperty> sortProperties, SelfReportQueryContext selfReportQueryContext) {

		List<Order> orderList = new ArrayList<>();
		CriteriaBuilder cb = selfReportQueryContext.getCriteriaBuilder();
		From<?, SelfReport> root = selfReportQueryContext.getRoot();
		SelfReportJoins joins = selfReportQueryContext.getJoins();

		if (sortProperties != null && !sortProperties.isEmpty()) {
			for (SortProperty sortProperty : sortProperties) {
				CriteriaBuilderHelper.OrderBuilder orderBuilder = CriteriaBuilderHelper.createOrderBuilder(cb, sortProperty.ascending);
				final List<Order> order;
				switch (sortProperty.propertyName) {
				case SelfReportIndexDto.UUID:
				case SelfReportIndexDto.TYPE:
				case SelfReportIndexDto.REPORT_DATE:
				case SelfReportIndexDto.DISEASE:
				case SelfReportIndexDto.SEX:
				case SelfReportIndexDto.INVESTIGATION_STATUS:
				case SelfReportIndexDto.PROCESSING_STATUS:
					order = orderBuilder.build(root.get(sortProperty.propertyName));
					break;
				case SelfReportIndexDto.FIRST_NAME:
				case SelfReportIndexDto.LAST_NAME:
				case SelfReportIndexDto.EMAIL:
				case SelfReportIndexDto.PHONE_NUMBER:
					order = orderBuilder.build(cb.lower(root.get(sortProperty.propertyName)));
					break;
				case SelfReportIndexDto.DISTRICT:
					Join<Location, District> district = joins.getAddressJoins().getDistrict();
					order = orderBuilder.build(cb.lower(district.get(District.NAME)));
					break;
				case SelfReportIndexDto.BIRTH_DATE:
					order =
						orderBuilder.build(root.get(SelfReport.BIRTHDATE_YYYY), root.get(SelfReport.BIRTHDATE_MM), root.get(SelfReport.BIRTHDATE_DD));
					break;
				case SelfReportDto.ADDRESS:
					Join<SelfReport, Location> location = joins.getAddress();
					order = orderBuilder.build(
						cb.lower(location.get(Location.STREET)),
						cb.lower(location.get(Location.HOUSE_NUMBER)),
						cb.lower(location.get(Location.CITY)),
						cb.lower(location.get(Location.POSTAL_CODE)));
					break;
				case SelfReportIndexDto.RESPONSIBLE_USER:
					Join<SelfReport, User> responsibleUser = joins.getResponsibleUser();
					order = orderBuilder.build(cb.lower(responsibleUser.get(User.FIRST_NAME)), cb.lower(responsibleUser.get(User.LAST_NAME)));
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				orderList.addAll(order);
			}
		} else {
			orderList.add(cb.desc(root.get(SelfReport.CHANGE_DATE)));
		}

		return orderList;
	}

	@Override
	public List<SelfReportListEntryDto> getEntriesList(SelfReportCriteria selfReportCriteria, Integer first, Integer max) {
		return service.getEntriesList(selfReportCriteria, first, max);
	}

	@Override
	public void validate(SelfReportDto dto) throws ValidationRuntimeException {

		if (dto.getType() == null) {
			throw new ValidationRuntimeException(
				I18nProperties
					.getValidationError(Validations.required, I18nProperties.getPrefixCaption(SelfReportDto.I18N_PREFIX, SelfReportDto.TYPE)));
		}

		if (dto.getReportDate() == null) {
			throw new ValidationRuntimeException(
				I18nProperties
					.getValidationError(Validations.required, I18nProperties.getPrefixCaption(SelfReportDto.I18N_PREFIX, SelfReportDto.REPORT_DATE)));
		}

		if (dto.getDisease() == null) {
			throw new ValidationRuntimeException(
				I18nProperties
					.getValidationError(Validations.required, I18nProperties.getPrefixCaption(SelfReportDto.I18N_PREFIX, SelfReportDto.DISEASE)));
		}

		if (dto.getFirstName() == null) {
			throw new ValidationRuntimeException(
				I18nProperties
					.getValidationError(Validations.required, I18nProperties.getPrefixCaption(SelfReportDto.I18N_PREFIX, SelfReportDto.FIRST_NAME)));
		}

		if (dto.getLastName() == null) {
			throw new ValidationRuntimeException(
				I18nProperties
					.getValidationError(Validations.required, I18nProperties.getPrefixCaption(SelfReportDto.I18N_PREFIX, SelfReportDto.LAST_NAME)));
		}

		if (dto.getSex() == null) {
			throw new ValidationRuntimeException(
				I18nProperties
					.getValidationError(Validations.required, I18nProperties.getPrefixCaption(SelfReportDto.I18N_PREFIX, SelfReportDto.SEX)));
		}

		if (dto.getInvestigationStatus() == null) {
			throw new ValidationRuntimeException(
				I18nProperties.getValidationError(
					Validations.required,
					I18nProperties.getPrefixCaption(SelfReportDto.I18N_PREFIX, SelfReportDto.INVESTIGATION_STATUS)));
		}

		if (dto.getProcessingStatus() == null) {
			throw new ValidationRuntimeException(
				I18nProperties.getValidationError(
					Validations.required,
					I18nProperties.getPrefixCaption(SelfReportDto.I18N_PREFIX, SelfReportDto.PROCESSING_STATUS)));
		}

	}

	@Override
	public List<String> getArchivedUuidsSince(Date since) {
		throw new NotImplementedException();
	}

	@Override
	@RightsAllowed(UserRight._SELF_REPORT_DELETE)
	public void delete(String uuid, DeletionDetails deletionDetails) {
		SelfReport selfReport = service.getByUuid(uuid);

		if (!service.inJurisdictionOrOwned(selfReport)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.messageSelfReportOutsideJurisdictionDeletionDenied));
		}

		service.delete(selfReport, deletionDetails);
	}

	@Override
	@RightsAllowed(UserRight._SELF_REPORT_DELETE)
	public void restore(String uuid) {
		super.restore(uuid);
	}

	@Override
	@RightsAllowed(UserRight._SELF_REPORT_DELETE)
	public List<ProcessedEntity> delete(List<String> uuids, DeletionDetails deletionDetails) {
		throw new NotImplementedException();
	}

	@Override
	@RightsAllowed(UserRight._SELF_REPORT_DELETE)
	public List<ProcessedEntity> restore(List<String> uuids) {
		throw new NotImplementedException();
	}

	@Override
	@RightsAllowed(UserRight._SELF_REPORT_ARCHIVE)
	public ProcessedEntity archive(String entityUuid, Date endOfProcessingDate) {
		return super.archive(entityUuid, endOfProcessingDate);
	}

	@Override
	@RightsAllowed(UserRight._SELF_REPORT_ARCHIVE)
	public List<ProcessedEntity> archive(List<String> entityUuids) {
		return super.archive(entityUuids);
	}

	@Override
	@RightsAllowed(UserRight._SELF_REPORT_ARCHIVE)
	public List<ProcessedEntity> dearchive(List<String> entityUuids, String dearchiveReason) {
		return super.dearchive(entityUuids, dearchiveReason);
	}

	@Override
	@RightsAllowed(UserRight._SELF_REPORT_PROCESS)
	public void markProcessed(SelfReportReferenceDto selfReportRef, CaseReferenceDto caze) {
		SelfReport selfReport = service.getByReferenceDto(selfReportRef);
		selfReport.setProcessingStatus(SelfReportProcessingStatus.PROCESSED);
		selfReport.setResultingCase(caseService.getByReferenceDto(caze));

		service.ensurePersisted(selfReport);
	}

	@Override
	@RightsAllowed(UserRight._SELF_REPORT_PROCESS)
	public void markProcessed(SelfReportReferenceDto selfReportRef, ContactReferenceDto contactRef) {
		SelfReport selfReport = service.getByReferenceDto(selfReportRef);
		selfReport.setProcessingStatus(SelfReportProcessingStatus.PROCESSED);
		selfReport.setResultingContact(contactService.getByReferenceDto(contactRef));

		service.ensurePersisted(selfReport);
	}

	@Override
	public boolean isProcessed(SelfReportReferenceDto reference) {
		return service.exists(
			(cb, root, cq) -> cb.and(
				cb.equal(root.get(SelfReport.UUID), reference.getUuid()),
				cb.equal(root.get(SelfReport.PROCESSING_STATUS), SelfReportProcessingStatus.PROCESSED)));
	}

	@Override
	public boolean existsUnlinkedCOntactWithCaseReferenceNumber(String caseReferenceNumber) {
		if (StringUtils.isBlank(caseReferenceNumber)) {
			return false;
		}

		return contactService.exists(
			(cb, root, cq) -> cb.and(
				cb.notEqual(root.get(Contact.DELETED), true),
				cb.notEqual(root.get(Contact.ARCHIVED), true),
				cb.equal(root.get(Contact.CASE_REFERENCE_NUMBER), caseReferenceNumber),
				cb.isNull(root.get(Contact.CAZE))));
	}

	@Override
	@RightsAllowed(UserRight._SELF_REPORT_PROCESS)
	public void linkContactsToCaseByReferenceNumber(CaseReferenceDto cazeRef) {
		Case caze = caseService.getByReferenceDto(cazeRef);
		contactService.findBy(new ContactCriteria().caseReferenceNumber(caze.getCaseReferenceNumber()).withCase(false), userService.getCurrentUser())
			.forEach(contact -> {
				contact.setCaze(caze);
				contactService.ensurePersisted(contact);
			});
	}

	@Override
	public boolean existsReferencedCaseReport(String caseReference) {
		return service.exists(
			(cb, root, cq) -> cb.and(
				cb.notEqual(root.get(SelfReport.DELETED), true),
				cb.notEqual(root.get(SelfReport.ARCHIVED), true),
				cb.equal(root.get(SelfReport.TYPE), SelfReportType.CASE),
				cb.equal(root.get(SelfReport.CASE_REFERENCE), caseReference),
				cb.not(cb.equal(root.get(SelfReport.PROCESSING_STATUS), SelfReportProcessingStatus.PROCESSED))));
	}

	@Override
	protected SelfReport fillOrBuildEntity(SelfReportDto source, SelfReport target, boolean checkChangeDate) {
		target = DtoHelper.fillOrBuildEntity(source, target, SelfReport::new, checkChangeDate);

		target.setType(source.getType());
		target.setReportDate(source.getReportDate());
		target.setCaseReference(source.getCaseReference());
		target.setDisease(source.getDisease());
		target.setDiseaseDetails(source.getDiseaseDetails());
		target.setDiseaseVariant(source.getDiseaseVariant());
		target.setDiseaseVariantDetails(source.getDiseaseVariantDetails());
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
		target.setDateOfTest(source.getDateOfTest());
		target.setDateOfSymptoms(source.getDateOfSymptoms());
		target.setWorkplace(source.getWorkplace());
		target.setDateWorkplace(source.getDateWorkplace());
		target.setIsolationDate(source.getIsolationDate());
		target.setContactDate(source.getContactDate());
		target.setComment(source.getComment());
		target.setResponsibleUser(userService.getByReferenceDto(source.getResponsibleUser()));
		target.setInvestigationStatus(source.getInvestigationStatus());
		target.setProcessingStatus(source.getProcessingStatus());

		target.setDeleted(source.isDeleted());
		target.setDeletionReason(source.getDeletionReason());
		target.setOtherDeletionReason(source.getOtherDeletionReason());

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
		target.setDiseaseDetails(source.getDiseaseDetails());
		target.setDiseaseVariant(source.getDiseaseVariant());
		target.setDiseaseVariantDetails(source.getDiseaseVariantDetails());
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
		target.setDateOfTest(source.getDateOfTest());
		target.setDateOfSymptoms(source.getDateOfSymptoms());
		target.setWorkplace(source.getWorkplace());
		target.setDateWorkplace(source.getDateWorkplace());
		target.setIsolationDate(source.getIsolationDate());
		target.setContactDate(source.getContactDate());
		target.setComment(source.getComment());
		target.setResponsibleUser(UserFacadeEjb.toReferenceDto(source.getResponsibleUser()));
		target.setInvestigationStatus(source.getInvestigationStatus());
		target.setProcessingStatus(source.getProcessingStatus());

		target.setDeleted(source.isDeleted());
		target.setDeletionReason(source.getDeletionReason());
		target.setOtherDeletionReason(source.getOtherDeletionReason());

		return target;
	}

	@Override
	protected SelfReportReferenceDto toRefDto(SelfReport selfReport) {
		return new SelfReportReferenceDto(selfReport.getUuid());
	}

	@Override
	protected void pseudonymizeDto(SelfReport source, SelfReportDto dto, Pseudonymizer<SelfReportDto> pseudonymizer, boolean inJurisdiction) {
		if (dto != null) {
			pseudonymizer.pseudonymizeDto(SelfReportDto.class, dto, inJurisdiction, e -> {
				pseudonymizer.pseudonymizeUser(source.getResponsibleUser(), userService.getCurrentUser(), dto::setResponsibleUser, dto);
			});
		}
	}

	@Override
	protected void restorePseudonymizedDto(
		SelfReportDto dto,
		SelfReportDto existingDto,
		SelfReport entity,
		Pseudonymizer<SelfReportDto> pseudonymizer) {
		if (existingDto != null) {
			boolean inJurisdiction = service.inJurisdictionOrOwned(entity);
			pseudonymizer.restorePseudonymizedValues(SelfReportDto.class, dto, existingDto, inJurisdiction);
			pseudonymizer.restoreUser(entity.getResponsibleUser(), userService.getCurrentUser(), dto, dto::setResponsibleUser);
		}
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
