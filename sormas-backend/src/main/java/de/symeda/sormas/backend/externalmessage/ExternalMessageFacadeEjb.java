package de.symeda.sormas.backend.externalmessage;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.CannotProceedException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportReferenceDto;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.customizableenum.CustomEnumNotFoundException;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageAdapterFacade;
import de.symeda.sormas.api.externalmessage.ExternalMessageCriteria;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageFacade;
import de.symeda.sormas.api.externalmessage.ExternalMessageFetchResult;
import de.symeda.sormas.api.externalmessage.ExternalMessageIndexDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageReferenceDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageResult;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.ExternalMessageType;
import de.symeda.sormas.api.externalmessage.NewMessagesState;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.systemevents.SystemEventDto;
import de.symeda.sormas.api.systemevents.SystemEventType;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReport;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReportFacadeEjb;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReportService;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.customizableenum.CustomizableEnumFacadeEjb;
import de.symeda.sormas.backend.externalmessage.labmessage.SampleReport;
import de.symeda.sormas.backend.externalmessage.labmessage.SampleReportFacadeEjb;
import de.symeda.sormas.backend.externalmessage.labmessage.TestReport;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.systemevent.sync.SyncFacadeEjb;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "ExternalMessageFacade")
@RightsAllowed(UserRight._EXTERNAL_MESSAGE_VIEW)
public class ExternalMessageFacadeEjb implements ExternalMessageFacade {

	public static final List<String> VALID_SORT_PROPERTY_NAMES = Arrays.asList(
		ExternalMessageIndexDto.UUID,
		ExternalMessageIndexDto.TYPE,
		ExternalMessageIndexDto.PERSON_FIRST_NAME,
		ExternalMessageIndexDto.PERSON_LAST_NAME,
		ExternalMessageIndexDto.PERSON_POSTAL_CODE,
		ExternalMessageIndexDto.REPORTER_NAME,
		ExternalMessageIndexDto.REPORTER_POSTAL_CODE,
		ExternalMessageIndexDto.MESSAGE_DATE_TIME,
		ExternalMessageIndexDto.STATUS,
		ExternalMessageIndexDto.DISEASE,
		ExternalMessageIndexDto.DISEASE_VARIANT);

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private ExternalMessageService externalMessageService;

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;
	@EJB
	private SyncFacadeEjb.SyncFacadeEjbLocal syncFacadeEjb;
	@EJB
	private SampleReportFacadeEjb.SampleReportFacadeEjbLocal sampleReportFacade;
	@EJB
	private SurveillanceReportService surveillanceReportService;
	@EJB
	private SampleService sampleService;
	@EJB
	private CaseService caseService;
	@EJB
	private UserService userService;

	@EJB
	private CustomizableEnumFacadeEjb.CustomizableEnumFacadeEjbLocal customizableEnumFacade;

	ExternalMessage fillOrBuildEntity(@NotNull ExternalMessageDto source, ExternalMessage target, boolean checkChangeDate) {

		target = DtoHelper.fillOrBuildEntity(source, target, ExternalMessage::new, checkChangeDate);

		target.setType(source.getType());
		target.setExternalMessageDetails(source.getExternalMessageDetails());
		target.setDisease(source.getDisease());
		target.setDiseaseVariant(source.getDiseaseVariant());
		target.setDiseaseVariantDetails(source.getDiseaseVariantDetails());
		target.setMessageDateTime(source.getMessageDateTime());
		target.setPersonBirthDateDD(source.getPersonBirthDateDD());
		target.setPersonBirthDateMM(source.getPersonBirthDateMM());
		target.setPersonBirthDateYYYY(source.getPersonBirthDateYYYY());
		target.setPersonCity(source.getPersonCity());
		target.setPersonFirstName(source.getPersonFirstName());
		target.setPersonHouseNumber(source.getPersonHouseNumber());
		target.setPersonLastName(source.getPersonLastName());
		target.setPersonPostalCode(source.getPersonPostalCode());
		target.setPersonSex(source.getPersonSex());
		target.setPersonPresentCondition(source.getPersonPresentCondition());
		target.setPersonStreet(source.getPersonStreet());
		target.setStatus(source.getStatus());
		target.setPersonPhone(source.getPersonPhone());
		target.setPersonEmail(source.getPersonEmail());
		target.setReporterCity(source.getReporterCity());
		target.setReporterExternalIds(source.getReporterExternalIds());
		target.setReporterName(source.getReporterName());
		target.setReporterPostalCode(source.getReporterPostalCode());
		target.setReportMessageId(source.getReportMessageId());

		target.setReportId(source.getReportId());
		if (source.getAssignee() != null) {
			target.setAssignee(userService.getByReferenceDto(source.getAssignee()));
		} else {
			target.setAssignee(null);
		}
		if (source.getSampleReports() != null) {
			List<SampleReport> sampleReports = new ArrayList<>();
			for (SampleReportDto s : source.getSampleReports()) {
				SampleReport sampleReport = sampleReportFacade.fromDto(s, target, false);
				sampleReports.add(sampleReport);
			}
			target.setSampleReports(sampleReports);
		}
		target.setSurveillanceReport(surveillanceReportService.getByReferenceDto(source.getSurveillanceReport()));
		return target;
	}

	private ExternalMessageDto saveWithFallback(ExternalMessageDto dto) {
		try {
			return save(dto, true, true);
		} catch (Exception e) {
			logger.error(
				String.format(
					"Could not save full external message with UUID %s, falling back to saving minimal version. Underlying error: %s",
					dto.getUuid(),
					e.getMessage()));
			ExternalMessageDto minimalMessage = ExternalMessageDto.build();
			minimalMessage.setUuid(dto.getUuid());
			minimalMessage.setExternalMessageDetails(dto.getExternalMessageDetails());
			minimalMessage.setStatus(dto.getStatus());
			minimalMessage.setType(dto.getType());
			minimalMessage.setMessageDateTime(dto.getMessageDateTime());

			return save(minimalMessage);
		}
	}

	@Override
	public ExternalMessageDto save(@Valid ExternalMessageDto dto) {
		return save(dto, true, false);
	}

	public ExternalMessageDto save(@Valid ExternalMessageDto dto, boolean checkChangeDate, boolean newTransaction) {
		ExternalMessage externalMessage = externalMessageService.getByUuid(dto.getUuid());

		validate(dto);

		externalMessage = fillOrBuildEntity(dto, externalMessage, checkChangeDate);

		// If it is a LabMessage and it has not set a DiseaseVariant, an attempt is made to determine this from the attached TestReports.
		if (ExternalMessageType.LAB_MESSAGE.equals(externalMessage.getType())
			&& externalMessage.getDiseaseVariant() == null
			&& CollectionUtils.isNotEmpty(externalMessage.getSampleReports())) {
			Set<TestReport> positiveTestReportsWithDiseaseVariant = externalMessage.getSampleReports()
				.stream()
				.flatMap(sampleReport -> sampleReport.getTestReports().stream())
				.filter(
					testReportDto -> PathogenTestResultType.POSITIVE.equals(testReportDto.getTestResult())
						&& testReportDto.getTestedDiseaseVariant() != null)
				.collect(Collectors.toSet());
			Set<String> diseaseVariants =
				positiveTestReportsWithDiseaseVariant.stream().map(TestReport::getTestedDiseaseVariant).collect(Collectors.toSet());
			// If we can't determine exactly one DiseaseVariant then we don't need to continue because we can only set one "main" DiseaseVariant
			if (diseaseVariants.size() == 1) {
				Set<String> diseaseVariantDetails =
					positiveTestReportsWithDiseaseVariant.stream().map(TestReport::getTestedDiseaseVariantDetails).collect(Collectors.toSet());
				// If we can't determine exact disease variant details then we don't need to continue because we can only set one "main" disease variant details
				if (diseaseVariantDetails.size() == 1) {
					String diseaseVariant = diseaseVariants.stream().findFirst().get();
					try {
						externalMessage.setDiseaseVariant(
							customizableEnumFacade.getEnumValue(CustomizableEnumType.DISEASE_VARIANT, diseaseVariant, externalMessage.getDisease()));
						if (diseaseVariantDetails.stream().allMatch(Objects::nonNull)) {
							externalMessage.setDiseaseVariantDetails(diseaseVariantDetails.stream().findFirst().get());
						}
					} catch (CustomEnumNotFoundException e) {
						throw new RuntimeException("Could not find DiseaseVariant " + diseaseVariant, e);
					}
				}
			}
		}

		if (newTransaction) {
			externalMessageService.ensurePersistedInNewTransaction(externalMessage);
		} else {
			externalMessageService.ensurePersisted(externalMessage);
		}
		return toDto(externalMessage);
	}

	@Override
	public void validate(ExternalMessageDto externalMessageDto) {
		if (externalMessageDto.getSurveillanceReport() != null) {
			SurveillanceReport surveillanceReport = surveillanceReportService.getByReferenceDto(externalMessageDto.getSurveillanceReport());
			if (externalMessageDto.getSampleReportsNullSafe()
				.stream()
				.map(sampleRep -> sampleRep.getSample())
				.map(sampleRef -> sampleService.getByReferenceDto(sampleRef))
				.anyMatch(
					sample -> sample != null
						&& (sample.getAssociatedContact() != null
							|| sample.getAssociatedEventParticipant() != null
							|| (sample.getAssociatedCase() != null
								&& !sample.getAssociatedCase().getUuid().equals(surveillanceReport.getCaze().getUuid()))))) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.externalMessageRefersToMultipleEntities));
			}
		}
	}

	public ExternalMessageDto toDto(ExternalMessage source) {

		if (source == null) {
			return null;
		}
		ExternalMessageDto target = new ExternalMessageDto();
		DtoHelper.fillDto(target, source);

		target.setType(source.getType());
		target.setExternalMessageDetails(source.getExternalMessageDetails());
		target.setDisease(source.getDisease());
		target.setDiseaseVariant(source.getDiseaseVariant());
		target.setDiseaseVariantDetails(source.getDiseaseVariantDetails());
		target.setMessageDateTime(source.getMessageDateTime());
		target.setPersonBirthDateDD(source.getPersonBirthDateDD());
		target.setPersonBirthDateMM(source.getPersonBirthDateMM());
		target.setPersonBirthDateYYYY(source.getPersonBirthDateYYYY());
		target.setPersonCity(source.getPersonCity());
		target.setPersonFirstName(source.getPersonFirstName());
		target.setPersonHouseNumber(source.getPersonHouseNumber());
		target.setPersonLastName(source.getPersonLastName());
		target.setPersonPostalCode(source.getPersonPostalCode());
		target.setPersonSex(source.getPersonSex());
		target.setPersonPresentCondition(source.getPersonPresentCondition());
		target.setPersonStreet(source.getPersonStreet());
		target.setPersonPhone(source.getPersonPhone());
		target.setPersonEmail(source.getPersonEmail());
		target.setReporterCity(source.getReporterCity());
		target.setReporterExternalIds(source.getReporterExternalIds());
		target.setReporterName(source.getReporterName());
		target.setReporterPostalCode(source.getReporterPostalCode());
		target.setStatus(source.getStatus());
		target.setReportMessageId(source.getReportMessageId());

		target.setReportId(source.getReportId());
		if (source.getSampleReports() != null) {
			target.setSampleReports(source.getSampleReports().stream().map(sampleReportFacade::toDto).collect(toList()));
		}
		if (source.getSurveillanceReport() != null) {
			target.setSurveillanceReport(SurveillanceReportFacadeEjb.toReferenceDto(source.getSurveillanceReport()));
		}
		if (source.getAssignee() != null) {
			target.setAssignee(source.getAssignee().toReference());
		}

		return target;
	}

	@Override
	public ExternalMessageDto getByUuid(String uuid) {
		return toDto(externalMessageService.getByUuid(uuid));
	}

	@Override
	@RightsAllowed(UserRight._EXTERNAL_MESSAGE_DELETE)
	public void delete(String uuid) {
		externalMessageService.deletePermanent(externalMessageService.getByUuid(uuid));
	}

	@Override
	@RightsAllowed(UserRight._EXTERNAL_MESSAGE_DELETE)
	public void delete(List<String> uuids) {
		List<String> deletedExternalMessageUuids = new ArrayList<>();
		List<ExternalMessage> externalMessagesToBeDeleted = externalMessageService.getByUuids(uuids);

		if (externalMessagesToBeDeleted != null) {
			externalMessagesToBeDeleted.forEach(externalMessageToBeDeleted -> {
				if (externalMessageToBeDeleted.getStatus() != ExternalMessageStatus.PROCESSED) {
					try {
						externalMessageService.deletePermanent(externalMessageToBeDeleted);
						deletedExternalMessageUuids.add(externalMessageToBeDeleted.getUuid());
					} catch (Exception e) {
						logger.error("The external message with uuid:" + externalMessageToBeDeleted.getUuid() + "could not be deleted");
					}
				}
			});
		}
	}

	@Override
	@RightsAllowed(UserRight._PERFORM_BULK_OPERATIONS_EXTERNAL_MESSAGES)
	public void bulkAssignExternalMessages(List<String> uuids, UserReferenceDto userRef) {
		List<ExternalMessage> externalMessages = externalMessageService.getByUuids(uuids);
		User user = userService.getByReferenceDto(userRef);
		for (ExternalMessage externalMessage : externalMessages) {
			externalMessage.setAssignee(user);
			externalMessageService.ensurePersisted(externalMessage);
		}
	}

	@Override
	public List<ExternalMessageDto> getForSample(SampleReferenceDto sample) {
		return externalMessageService.getForSample(sample).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public boolean isProcessed(String uuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Boolean> cq = cb.createQuery(Boolean.class);
		Root<ExternalMessage> from = cq.from(ExternalMessage.class);

		Predicate filter = cb.and(cb.equal(from.get(ExternalMessage.UUID), uuid));

		cq.where(filter);
		cq.select(cb.equal(from.get(ExternalMessage.STATUS), ExternalMessageStatus.PROCESSED));

		return BooleanUtils.isTrue(QueryHelper.getSingleResult(em, cq));
	}

	@Override
	public long count(ExternalMessageCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ExternalMessage> externalMessage = cq.from(ExternalMessage.class);

		Predicate filter = externalMessageService.createDefaultFilter(cb, externalMessage);
		if (criteria != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, externalMessageService.buildCriteriaFilter(cb, externalMessage, criteria));
		}
		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(externalMessage));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public List<ExternalMessageIndexDto> getIndexList(
		ExternalMessageCriteria criteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {
		List<Long> indexListIds = getIndexListIds(criteria, first, max, sortProperties);

		List<ExternalMessageIndexDto> messages = new ArrayList<>();

		IterableHelper.executeBatched(indexListIds, ModelConstants.PARAMETER_LIMIT, batchedIds -> {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ExternalMessageIndexDto> cq = cb.createQuery(ExternalMessageIndexDto.class);
			Root<ExternalMessage> externalMessage = cq.from(ExternalMessage.class);
			Join<ExternalMessage, User> userJoin = externalMessage.join(ExternalMessage.ASSIGNEE, JoinType.LEFT);

			cq.multiselect(
				externalMessage.get(ExternalMessage.UUID),
				externalMessage.get(ExternalMessage.TYPE),
				externalMessage.get(ExternalMessage.MESSAGE_DATE_TIME),
				externalMessage.get(ExternalMessage.REPORTER_NAME),
				externalMessage.get(ExternalMessage.REPORTER_POSTAL_CODE),
				externalMessage.get(ExternalMessage.DISEASE),
				externalMessage.get(ExternalMessage.DISEASE_VARIANT),
				externalMessage.get(ExternalMessage.PERSON_FIRST_NAME),
				externalMessage.get(ExternalMessage.PERSON_LAST_NAME),
				externalMessage.get(ExternalMessage.PERSON_BIRTH_DATE_YYYY),
				externalMessage.get(ExternalMessage.PERSON_BIRTH_DATE_MM),
				externalMessage.get(ExternalMessage.PERSON_BIRTH_DATE_DD),
				externalMessage.get(ExternalMessage.PERSON_POSTAL_CODE),
				externalMessage.get(ExternalMessage.STATUS),
				userJoin.get(User.UUID),
				userJoin.get(User.FIRST_NAME),
				userJoin.get(User.LAST_NAME));

			cq.where(externalMessage.get(ExternalMessage.ID).in(batchedIds));
			cq.orderBy(getOrderList(sortProperties, cb, externalMessage));
			cq.distinct(true);

			messages.addAll(QueryHelper.getResultList(em, cq, null, null));
		});

		return messages;
	}

	public List<Long> getIndexListIds(ExternalMessageCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<ExternalMessage> externalMessage = cq.from(ExternalMessage.class);

		List<Selection<?>> selections = new ArrayList<>();
		selections.add(externalMessage.get(ExternalMessage.ID));

		List<Order> orderList = getOrderList(sortProperties, cb, externalMessage);
		List<Expression<?>> sortColumns = orderList.stream().map(Order::getExpression).collect(Collectors.toList());
		selections.addAll(sortColumns);

		cq.multiselect(selections);

		Predicate filter = externalMessageService.createDefaultFilter(cb, externalMessage);

		if (criteria != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, externalMessageService.buildCriteriaFilter(cb, externalMessage, criteria));
		}

		if (filter != null) {
			cq.where(filter);
		}

		// Distinct is necessary here to avoid duplicate results due to the user role join in taskService.createAssigneeFilter
		cq.distinct(true);
		cq.orderBy(orderList);

		return QueryHelper.getResultList(em, cq, first, max).stream().map(t -> t.get(0, Long.class)).collect(Collectors.toList());
	}

	private List<Order> getOrderList(List<SortProperty> sortProperties, CriteriaBuilder cb, Root<ExternalMessage> externalMessageRoot) {
		List<Order> order = new ArrayList<>();

		if (!CollectionUtils.isEmpty(sortProperties)) {
			for (SortProperty sortProperty : sortProperties) {
				if (ExternalMessageIndexDto.PERSON_BIRTH_DATE.equals(sortProperty.propertyName)) {
					Expression<?> birthdateYYYY = externalMessageRoot.get(ExternalMessage.PERSON_BIRTH_DATE_YYYY);
					order.add(sortProperty.ascending ? cb.asc(birthdateYYYY) : cb.desc(birthdateYYYY));
					Expression<?> birthdateMM = externalMessageRoot.get(ExternalMessage.PERSON_BIRTH_DATE_MM);
					order.add(sortProperty.ascending ? cb.asc(birthdateMM) : cb.desc(birthdateMM));
					Expression<?> birthdateDD = externalMessageRoot.get(ExternalMessage.PERSON_BIRTH_DATE_DD);
					order.add(sortProperty.ascending ? cb.asc(birthdateDD) : cb.desc(birthdateDD));
				} else if (VALID_SORT_PROPERTY_NAMES.contains(sortProperty.propertyName)) {
					Expression<?> expression = externalMessageRoot.get(sortProperty.propertyName);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
				}
			}
		}

		order.add(cb.desc(externalMessageRoot.get(ExternalMessage.MESSAGE_DATE_TIME)));

		return order;
	}

	public Page<ExternalMessageIndexDto> getIndexPage(
		ExternalMessageCriteria criteria,
		Integer offset,
		Integer size,
		List<SortProperty> sortProperties) {
		List<ExternalMessageIndexDto> labMessageIndexList = getIndexList(criteria, offset, size, sortProperties);
		long totalElementCount = count(criteria);
		return new Page<>(labMessageIndexList, offset, size, totalElementCount);
	}

	/**
	 * This method marks the previously unfinished system events as UNCLEAR(if any exists) and creates a new event with status STARTED.
	 * If the fetching succeeds, the status of the currentSync is changed to SUCCESS.
	 * In case of any Exception, the status of the currentSync is changed to ERROR.
	 *
	 * @return An indication whether the fetching of new labMessage was successful. If it was not, an error message meant for UI users.
	 */
	@Override
	@RightsAllowed({
		UserRight._SYSTEM,
		UserRight._EXTERNAL_MESSAGE_VIEW })
	public ExternalMessageFetchResult fetchAndSaveExternalMessages(Date since) {

		SystemEventDto currentSync = syncFacadeEjb.startSyncFor(SystemEventType.FETCH_EXTERNAL_MESSAGES);

		try {
			return fetchAndSaveExternalMessages(currentSync, since);
		} catch (CannotProceedException e) {
			syncFacadeEjb.reportSyncErrorWithTimestamp(currentSync, e.getMessage());
			return new ExternalMessageFetchResult(false, NewMessagesState.UNCLEAR, e.getMessage());
		} catch (NamingException e) {
			syncFacadeEjb.reportSyncErrorWithTimestamp(currentSync, e.getMessage());
			return new ExternalMessageFetchResult(false, NewMessagesState.UNCLEAR, I18nProperties.getString(Strings.errorLabResultsAdapterNotFound));
		} catch (Exception t) {
			syncFacadeEjb.reportSyncErrorWithTimestamp(currentSync, t.getMessage());
			throw t;
		}
	}

	protected ExternalMessageFetchResult fetchAndSaveExternalMessages(SystemEventDto currentSync, Date since) throws NamingException {
		if (since == null) {
			since = syncFacadeEjb.findLastSyncDateFor(SystemEventType.FETCH_EXTERNAL_MESSAGES);
		}
		ExternalMessageResult<List<ExternalMessageDto>> externalMessageResult = fetchExternalMessages(since);
		if (externalMessageResult.isSuccess()) {
			externalMessageResult.getValue().forEach(this::saveWithFallback);
			// we have successfully completed our synchronization
			syncFacadeEjb.reportSuccessfulSyncWithTimestamp(currentSync, externalMessageResult.getSynchronizationDate());

			return getSuccessfulFetchResult(externalMessageResult);
		} else {
			throw new CannotProceedException(externalMessageResult.getError());
		}
	}

	protected ExternalMessageResult<List<ExternalMessageDto>> fetchExternalMessages(Date since) throws NamingException {
		ExternalMessageAdapterFacade labResultsFacade = getExternalLabResultsFacade();
		return labResultsFacade.getExternalMessages(since);
	}

	private ExternalMessageAdapterFacade getExternalLabResultsFacade() throws NamingException {
		InitialContext ic = new InitialContext();
		String jndiName = configFacade.getExternalMessageAdapterJndiName();

		if (jndiName == null) {
			throw new CannotProceedException(I18nProperties.getValidationError(Validations.externalMessageConfigError));
		}

		return (ExternalMessageAdapterFacade) ic.lookup(jndiName);
	}

	@Override
	@PermitAll
	public String getExternalMessagesAdapterVersion() throws NamingException {
		ExternalMessageAdapterFacade labResultsFacade = getExternalLabResultsFacade();
		String version = I18nProperties.getCaption(Captions.versionIsMissing);
		try {
			version = labResultsFacade.getVersion();
		} catch (Exception e) {
		}
		return version;
	}

	private ExternalMessageFetchResult getSuccessfulFetchResult(ExternalMessageResult<List<ExternalMessageDto>> externalMessageResult) {
		if (isEmptyResult(externalMessageResult)) {
			return new ExternalMessageFetchResult(true, NewMessagesState.NO_NEW_MESSAGES, null);
		} else {
			return new ExternalMessageFetchResult(true, NewMessagesState.NEW_MESSAGES, null);
		}
	}

	private boolean isEmptyResult(ExternalMessageResult<List<ExternalMessageDto>> externalMessageResult) {
		return externalMessageResult.getValue() == null || externalMessageResult.getValue().isEmpty();
	}

	@Override
	public boolean exists(String uuid) {
		return externalMessageService.exists(uuid);
	}

	@Override
	public boolean existsExternalMessageForEntity(ReferenceDto entityRef) {
		if (CaseReferenceDto.class.equals(entityRef.getClass())) {
			return externalMessageService.countForCase(entityRef.getUuid()) > 0;
		} else if (ContactReferenceDto.class.equals(entityRef.getClass())) {
			return externalMessageService.countForContact(entityRef.getUuid()) > 0;
		} else if (EventParticipantReferenceDto.class.equals(entityRef.getClass())) {
			return externalMessageService.countForEventParticipant(entityRef.getUuid()) > 0;
		} else {
			throw new UnsupportedOperationException("Reference class" + entityRef.getClass() + " is not supported.");
		}
	}

	@Override
	public List<ExternalMessageDto> getByReportId(String reportId) {

		if (reportId == null) {
			return Collections.emptyList();
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ExternalMessage> cq = cb.createQuery(ExternalMessage.class);
		Root<ExternalMessage> from = cq.from(ExternalMessage.class);

		cq.where(cb.equal(from.get(ExternalMessage.REPORT_ID), reportId));

		return em.createQuery(cq).getResultList().stream().map(this::toDto).collect(toList());
	}

	@Override
	public boolean existsForwardedExternalMessageWith(String reportId) {

		List<ExternalMessageDto> relatedLabMessages = getByReportId(reportId);

		for (ExternalMessageDto labMessage : relatedLabMessages) {
			if (ExternalMessageStatus.FORWARDED.equals(labMessage.getStatus())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ExternalMessageDto getForSurveillanceReport(SurveillanceReportReferenceDto surveillanceReport) {
		return toDto(externalMessageService.getForSurveillanceReport(surveillanceReport));
	}

	public static ExternalMessageReferenceDto toReferenceDto(ExternalMessage entity) {

		if (entity == null) {
			return null;
		}

		return new ExternalMessageReferenceDto(entity.getUuid(), entity.toString());
	}

	@LocalBean
	@Stateless
	public static class ExternalMessageFacadeEjbLocal extends ExternalMessageFacadeEjb {

	}
}
